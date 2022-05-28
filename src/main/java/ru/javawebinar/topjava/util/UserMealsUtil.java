package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

       List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));

        List<UserMealWithExcess> mealsTo1 = filteredByOptional2Streams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo1.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesForEachDay = new HashMap<>();
        for (UserMeal userMeal : meals) {
            caloriesForEachDay.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum);
        }

        List<UserMealWithExcess> mealWithExcesses = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            LocalDateTime mealDateTime = userMeal.getDateTime();
            if (TimeUtil.isBetweenHalfOpen(mealDateTime.toLocalTime(), startTime, endTime)) {
                boolean excess = caloriesForEachDay.get(mealDateTime.toLocalDate()) > caloriesPerDay;
                mealWithExcesses.add(new UserMealWithExcess(mealDateTime, userMeal.getDescription(), userMeal.getCalories(),
                        excess));
            }
        }
        return mealWithExcesses;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesForEachDay = meals.stream()
                .collect(Collectors.groupingBy(um -> um.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));

        return meals
                .stream()
                .filter(um -> TimeUtil.isBetweenHalfOpen(um.getDateTime().toLocalTime(), startTime, endTime))
                .map(um -> new UserMealWithExcess(um.getDateTime(), um.getDescription(), um.getCalories(),
                        caloriesForEachDay.get(um.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByOptional2Streams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        class MealCollector implements Collector<UserMeal, Map<LocalDate, List <UserMeal>>, List<UserMealWithExcess>> {
            final private Map<LocalDate, Integer> caloriesForEachDay = new ConcurrentHashMap<>();

            @Override
            public Supplier<Map<LocalDate, List <UserMeal>>> supplier() {
                return HashMap::new;
            }

            @Override
            public BiConsumer<Map<LocalDate, List<UserMeal>>, UserMeal> accumulator() {
                return (mealsByDaysMap, userMeal) -> {
                    caloriesForEachDay.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum);
                    if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {
                        mealsByDaysMap.computeIfAbsent(userMeal.getDateTime().toLocalDate(), localDateKey ->
                                new ArrayList<>()).add(userMeal);
                    }
                };
            }

            @Override
            public BinaryOperator<Map<LocalDate, List<UserMeal>>> combiner() {
                return (map1, map2) -> {
                    map2.forEach((localDate, userMealList) ->
                            map1.merge(localDate, userMealList, (userMealList1, userMealList2) -> {
                                userMealList1.addAll(userMealList2);
                                return userMealList1;
                            }));
                    return map1;
                };
            }

            @Override
            public Function<Map<LocalDate, List <UserMeal>>, List<UserMealWithExcess>> finisher() {
                return userMealByDaysMap -> userMealByDaysMap
                        .values()
                        .stream()
                        .flatMap(listOfMeals -> listOfMeals.stream()
                                .map(userMeal -> new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(),
                                        caloriesForEachDay.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay)))
                        .collect(Collectors.toList());
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.CONCURRENT);
            }
        }
        return meals.stream().parallel().collect(new MealCollector());
    }
}

package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
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

        List<UserMealWithExcess> mealsTo2 = filteredByOptional2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo2.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesForEachDay = new HashMap<>();
        for (UserMeal userMeal : meals) {
            LocalDate userDate = userMeal.getDateTime().toLocalDate();
            caloriesForEachDay.merge(userDate, userMeal.getCalories(), Integer::sum);
        }

        List<UserMealWithExcess> mealWithExcesses = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            LocalDateTime userDateTime = userMeal.getDateTime();
            if (TimeUtil.isBetweenHalfOpen(userDateTime.toLocalTime(), startTime, endTime)) {
                boolean excess = caloriesForEachDay.get(userDateTime.toLocalDate()) > caloriesPerDay;
                mealWithExcesses.add(new UserMealWithExcess(userDateTime, userMeal.getDescription(), userMeal.getCalories(),
                        excess));
            }
        }
        return mealWithExcesses;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesForEachDay = meals.stream()
                .collect(Collectors.groupingBy(x -> x.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));

        return meals
                .stream()
                .filter(x -> TimeUtil.isBetweenHalfOpen(x.getDateTime().toLocalTime(), startTime, endTime))
                .map(x -> new UserMealWithExcess(x.getDateTime(), x.getDescription(), x.getCalories(),
                        caloriesForEachDay.get(x.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByOptional2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        class MealCollector implements Collector<UserMeal, Map<LocalDate, Integer>, List<UserMealWithExcess>> {
            @Override
            public Supplier<Map<LocalDate, Integer>> supplier() {
                return HashMap::new;
            }

            @Override
            public BiConsumer<Map<LocalDate, Integer>, UserMeal> accumulator() {
                return (map, val) -> map.merge(val.getDateTime().toLocalDate(), val.getCalories(), Integer::sum);
            }

            @Override
            public BinaryOperator<Map<LocalDate, Integer>> combiner() {
                return (map1, map2) -> {
                    map2.forEach((k, v) -> map1.merge(k, v, Integer::sum));
                    return map1;
                };
            }

            @Override
            public Function<Map<LocalDate, Integer>, List<UserMealWithExcess>> finisher() {
                return s -> meals
                        .stream()
                        .filter(x -> TimeUtil.isBetweenHalfOpen(x.getDateTime().toLocalTime(), startTime, endTime))
                        .map(x -> new UserMealWithExcess(x.getDateTime(), x.getDescription(), x.getCalories(),
                                s.get(x.getDateTime().toLocalDate()) > caloriesPerDay))
                        .collect(Collectors.toList());
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.CONCURRENT);
            }
        }
        return meals.stream().collect(new MealCollector());
    }
}

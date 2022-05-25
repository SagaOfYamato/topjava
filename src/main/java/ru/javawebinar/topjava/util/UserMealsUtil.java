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

        List<UserMealWithExcess> mealsTo1 = filteredByOptional2Streams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo1.forEach(System.out::println);
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
                .collect(Collectors.groupingBy(um -> um.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));

        return meals
                .stream()
                .filter(um -> TimeUtil.isBetweenHalfOpen(um.getDateTime().toLocalTime(), startTime, endTime))
                .map(um -> new UserMealWithExcess(um.getDateTime(), um.getDescription(), um.getCalories(),
                        caloriesForEachDay.get(um.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByOptional2Streams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        class MealCollector implements Collector<UserMeal, List <UserMealWithExcess>, List<UserMealWithExcess>> {
            private Map<LocalDate, Integer> caloriesForEachDay;

            @Override
            public Supplier<List <UserMealWithExcess>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List <UserMealWithExcess>, UserMeal> accumulator() {
                return (list, userMeal) -> list.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(),
                        userMeal.getCalories(), false));
            }

            @Override
            public BinaryOperator<List<UserMealWithExcess>> combiner() {
                return (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                };
            }

            @Override
            public Function<List<UserMealWithExcess>, List<UserMealWithExcess>> finisher() {
                return userMealWithExcessList -> {
                    caloriesForEachDay = userMealWithExcessList.stream().collect(Collectors.groupingBy(umwe -> umwe.getDateTime().toLocalDate(),
                            Collectors.summingInt(UserMealWithExcess::getCalories)));
                    {
                        return userMealWithExcessList.stream()
                                .filter(umwe -> TimeUtil.isBetweenHalfOpen(umwe.getDateTime().toLocalTime(), startTime, endTime))
                                .map(umwe -> new UserMealWithExcess(umwe.getDateTime(), umwe.getDescription(), umwe.getCalories(),
                                        caloriesForEachDay.get(umwe.getDateTime().toLocalDate()) > caloriesPerDay))
                                .collect(Collectors.toList());
                    }
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.CONCURRENT);
            }
        }
        return meals.stream().collect(new MealCollector());
    }
}

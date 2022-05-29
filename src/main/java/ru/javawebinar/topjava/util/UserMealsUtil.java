package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

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

        List<UserMealWithExcess> mealsTo = filteredByOptional2Cycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

//        System.out.println(filteredByOptional2Cycles(meals, LocalTime.of(7, 0), LocalTime.of(23, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByOptional2Cycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesForEachDay = new ConcurrentHashMap<>();
        List<UserMealWithExcess> mealWithExcesses = new CopyOnWriteArrayList<>();
        Map<LocalDate, AtomicBoolean> excessForEachDay = new ConcurrentHashMap<>();

        for (UserMeal userMeal : meals) {
            LocalDate mealDate = userMeal.getDateTime().toLocalDate();
            caloriesForEachDay.merge(mealDate, userMeal.getCalories(), Integer::sum);
            excessForEachDay.merge(mealDate, new AtomicBoolean(caloriesForEachDay.get(mealDate) > caloriesPerDay), (excess1, excess2) -> {
                excess1.lazySet(caloriesForEachDay.get(mealDate) > caloriesPerDay);
                return excess1;
            });

            LocalDateTime mealDateTime = userMeal.getDateTime();
            if (TimeUtil.isBetweenHalfOpen(mealDateTime.toLocalTime(), startTime, endTime)) {
                mealWithExcesses.add(new UserMealWithExcess(mealDateTime, userMeal.getDescription(), userMeal.getCalories(),
                        excessForEachDay.get(mealDate)));
            }
        }
        return mealWithExcesses;
    }
}

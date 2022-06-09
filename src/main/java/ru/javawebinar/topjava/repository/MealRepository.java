package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealRepository {

    Meal create(Meal meal);
    Meal read(int id);
    Meal update(Meal meal);
    boolean delete(int id);
    List<Meal> readAll();
    Meal getMealById(int id);
}
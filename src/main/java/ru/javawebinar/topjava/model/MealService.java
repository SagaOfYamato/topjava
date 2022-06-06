package ru.javawebinar.topjava.model;

public interface MealService {

    void create(Meal meal);
    Meal read(int id);
    boolean update(Meal client, int id);
    boolean delete(int id);
}
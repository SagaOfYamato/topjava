package ru.javawebinar.topjava.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealServiceImp implements MealService {
    private static final Map<Integer, Meal> MEAL_REPOSITORY_MAP = new ConcurrentHashMap<>();
    private static final AtomicInteger MEAL_ID_HOLDER = new AtomicInteger();

    @Override
    public void create(Meal meal) {
        final int mealId = MEAL_ID_HOLDER.incrementAndGet();
        meal.setId(mealId);
        MEAL_REPOSITORY_MAP.put(mealId, meal);
    }

    @Override
    public Meal read(int id) {
        return MEAL_REPOSITORY_MAP.get(id);
    }

    @Override
    public boolean update(Meal client, int id) {
        if (MEAL_REPOSITORY_MAP.containsKey(id)) {
            client.setId(id);
            MEAL_REPOSITORY_MAP.put(id, client);
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(int id) {
        return MEAL_REPOSITORY_MAP.remove(id) != null;
    }
}

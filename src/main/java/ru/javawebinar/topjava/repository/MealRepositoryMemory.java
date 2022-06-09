package ru.javawebinar.topjava.repository;
import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.MealServlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;

public class MealRepositoryMemory implements MealRepository {
    private static final Logger log = getLogger(MealRepositoryMemory.class);
    private final Map<Integer, Meal> mealRepositoryMap = new ConcurrentHashMap<>();
    private final AtomicInteger mealIdHolder = new AtomicInteger(0);
    MealsUtil mealsUtil = MealsUtil.getInstance();
    {
        for (Meal meal : mealsUtil.getMeals()) {
            mealRepositoryMap.put(mealIdHolder.incrementAndGet(), meal);
        }
    }

    @Override
    public Meal create(Meal meal) {
        final int mealId = mealIdHolder.incrementAndGet();
        Meal newMeal = new Meal(mealId, meal.getDateTime(), meal.getDescription(), meal.getCalories());
        mealRepositoryMap.put(mealId, newMeal);
        return newMeal;
    }

    @Override
    public Meal update(Meal meal) {
        Meal newMeal = mealRepositoryMap.get(meal.getId());
        log.debug("newmeal id = " + newMeal.getId());
        newMeal.setDateTime(meal.getDateTime());
        log.debug("newmeal DateTime = " + newMeal.getDateTime());
        newMeal.setDescription(meal.getDescription());
        log.debug("newmeal description = " + newMeal.getDescription());
        newMeal.setCalories(meal.getCalories());
        log.debug("newmeal calories = " + newMeal.getCalories());
        mealRepositoryMap.put(meal.getId(), newMeal);
        log.debug("newmeal put: " + mealRepositoryMap.get(newMeal.getId()));
        return newMeal;
    }

    @Override
    public boolean delete(int id) {
        boolean result = mealRepositoryMap.remove(id) != null;
        mealIdHolder.decrementAndGet();
        return result;
    }

    @Override
    public List<Meal> readAll() {
        return new ArrayList<>(mealRepositoryMap.values());
    }

    @Override
    public Meal getMealById(int id) {
        return mealRepositoryMap.get(id);
    }
}

package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.MealServlet;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counterOfMeal = new AtomicInteger(0);

    {
        for (Meal meal : MealsUtil.meals) {
            save(1, meal);
        }
    }

    @Override
    public Meal save(int userId, Meal meal) {
        if (userId != SecurityUtil.authUserId()) {
            return null;
        }
        if (!repository.containsKey(userId) || repository.get(userId) == null) {
            Map<Integer, Meal> mealRepository = new ConcurrentHashMap<>();
            repository.put(userId, mealRepository);
        }
        if (meal.isNew()) {
            meal.setId(counterOfMeal.incrementAndGet());
            repository.get(userId).put(meal.getId(), meal);
            return meal;
        }
        return repository.get(userId).computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int userId, int id) {
        if (userId != SecurityUtil.authUserId()) {
            return false;
        }
        return repository.get(userId).remove(id) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        if (userId != SecurityUtil.authUserId()) {
            return null;
        }
        return repository.get(userId).get(id);
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        if (userId != SecurityUtil.authUserId()) {
            return null;
        }
        List<Meal> mealsByUserId = new ArrayList<>(repository.get(userId).values());
        mealsByUserId.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));
        return mealsByUserId;
    }
}


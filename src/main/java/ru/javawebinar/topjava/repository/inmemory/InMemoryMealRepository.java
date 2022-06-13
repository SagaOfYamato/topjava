package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        for (Meal meal : MealsUtil.meals) {
            save(1, meal);
        }
    }

    @Override
    public Meal save(int userId, Meal meal) {
        log.info("save {}", meal);
        repository.computeIfAbsent(userId, (usId) -> new ConcurrentHashMap<>());
        Map<Integer, Meal> mealById = repository.get(userId);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            mealById.put(meal.getId(), meal);
            return meal;
        }
        return mealById.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int userId, int id) {
        log.info("delete {}", id);
        return repository.get(userId).remove(id) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        log.info("get {}", id);
        return repository.get(userId).get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        log.info("getAll");
        if (!repository.containsKey(userId)) {
            return null;
        }
        List<Meal> mealsByUserId = new ArrayList<>(repository.get(userId).values());
        mealsByUserId.sort(Comparator.comparing(Meal::getDate, Comparator.reverseOrder()));
        return mealsByUserId;
    }

    @Override
    public Collection<Meal> filter(int userId, LocalDate startDate, LocalDate endDate) {
        log.info("filter");
        List<Meal> mealsByUserId = getAll(userId);
        return mealsByUserId.stream()
                .filter(meal -> meal.getDate().compareTo(startDate) >= 0)
                .filter(meal -> meal.getDate().compareTo(endDate) <= 0)
                .collect(Collectors.toList());
    }
}


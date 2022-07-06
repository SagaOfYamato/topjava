package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {

    private final CrudMealRepository crudRepository;

    private final CrudUserRepository crudUserRepository;

    public DataJpaMealRepository(CrudMealRepository crudRepository, CrudUserRepository crudUserRepository) {
        this.crudRepository = crudRepository;
        this.crudUserRepository = crudUserRepository;
    }

    @Transactional
    @Override
    public Meal save(Meal meal, int userId) {
        if (!meal.isNew() && get(meal.id(), userId) == null) {
            return null;
        }
        meal.setUser(crudUserRepository.getReferenceById(userId));
        return crudRepository.save(meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        return crudRepository.deleteMealByIdAndUser(id, crudUserRepository.getReferenceById(userId)) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        return crudRepository.findMealByIdAndUser(id, crudUserRepository.getReferenceById(userId));
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudRepository.findAllByUserOrderByDateTimeDesc(crudUserRepository.getReferenceById(userId));
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudRepository.getBetweenHalfOpen(userId, startDateTime, endDateTime);
    }

    @Override
    public Meal getWithUser(int id, int userId) {
        return crudRepository.getWithUser(id, userId);
    }
}

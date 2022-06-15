package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class MealRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final MealService service;

    @Autowired
    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAll() {
        log.info("getAll");
        return MealsUtil.getTos(service.getAll(SecurityUtil.getUserId()), SecurityUtil.authUserCaloriesPerDay());
    }

    public Meal get(int id) {
        log.info("get {}", id);
        return service.get(SecurityUtil.getUserId(), id);
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        checkNew(meal);
        return service.create(SecurityUtil.getUserId(), meal);
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(SecurityUtil.getUserId(), id);
    }

    public void update(Meal meal, int id) {
        log.info("update {} with id={}", meal, id);
        assureIdConsistent(meal, id);
        service.update(SecurityUtil.getUserId(), meal);
    }

    public Collection<MealTo> filter(String startDate, String startTime, String endDate, String endTime) {
        log.info("filter {} {} - {} {}", startDate, startTime, endDate, endTime);

        LocalDate startDate1 = startDate == null || startDate.isEmpty() ?
                LocalDate.MIN : LocalDate.parse(startDate);
        LocalDate endDate1 = endDate == null || endDate.isEmpty() ?
                LocalDate.MAX : LocalDate.parse(endDate);
        LocalTime startTime1 = startTime == null || startTime.isEmpty() ?
                LocalTime.MIN : LocalTime.parse(startTime);
        LocalTime endTime1 = endTime == null || endTime.isEmpty() ?
                LocalTime.MAX : LocalTime.parse(endTime);

        List<Meal> mealsFilteredByDates = service.getFiltered(SecurityUtil.getUserId(), startDate1, endDate1);
        return MealsUtil.getFilteredTos(mealsFilteredByDates, SecurityUtil.authUserCaloriesPerDay(), startTime1, endTime1);
    }
}
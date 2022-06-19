package ru.javawebinar.topjava.service;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))

public class MealServiceTest extends TestCase {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(MEAL1_ID, USER_ID_IN_MEALTESTDATA);
        assertMatch(meal, meal1);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(meal8.getId(), USER_ID_IN_MEALTESTDATA));
    }

    @Test
    public void delete() {
        service.delete(MEAL1_ID, USER_ID_IN_MEALTESTDATA);
        assertThrows(NotFoundException.class, () -> service.get(MEAL1_ID, USER_ID_IN_MEALTESTDATA));
    }

    @Test
    public void deletedNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(meal8.getId(), USER_ID_IN_MEALTESTDATA));
    }

    @Test
    public void getBetweenInclusive() {
        LocalDate start = LocalDate.of(2020, Month.JANUARY, 30);
        LocalDate end = LocalDate.of(2020, Month.JANUARY, 30);
        List<Meal> allBetweenInclusive = service.getBetweenInclusive(start, end, USER_ID_IN_MEALTESTDATA);
        assertMatch(allBetweenInclusive, meal3, meal2, meal1);
    }

    @Test
    public void getAll() {
        List<Meal> all = service.getAll(USER_ID_IN_MEALTESTDATA);
        assertMatch(all, meal7, meal6, meal5, meal4, meal3, meal2, meal1);
    }

    @Test
    public void update() {
        Meal updated = MealTestData.getUpdated();
        service.update(updated, USER_ID_IN_MEALTESTDATA);
        assertMatch(service.get(updated.getId(), USER_ID_IN_MEALTESTDATA), getUpdated());
    }

    @Test
    public void updateNotFound() {
        assertThrows(NotFoundException.class, () -> service.update(meal8, USER_ID_IN_MEALTESTDATA));
    }

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID_IN_MEALTESTDATA);
        Integer newId = created.getId();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, USER_ID_IN_MEALTESTDATA), newMeal);
    }

    @Test
    public void duplicateDateTimeCreate() {
        assertThrows(DataAccessException.class, () ->
                service.create(new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Дубль по времени", 410), USER_ID_IN_MEALTESTDATA));
    }
}
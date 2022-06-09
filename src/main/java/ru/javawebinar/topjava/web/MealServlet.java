package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.repository.MealRepositoryMemory;
import ru.javawebinar.topjava.util.MealsUtil;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    //private static final long serialVersionUID = 1L;
    private static final String INSERT_OR_EDIT = "/meal.jsp";
    private static final String LIST_MEAL = "/listMeals.jsp";
    private MealRepository mealRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        mealRepository = new MealRepositoryMemory();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("redirect to doGet");
        String forward;
        List<MealTo> mealsTo = MealsUtil.filteredByStreams(mealRepository.readAll(), LocalTime.MIN, LocalTime.MAX,
                MealsUtil.CALORIES_PER_DAY);
        log.debug("action1 = " + mealsTo);
        String action = request.getParameter("action");

        if(action == null) {
            action = "listMeal";
        }
        log.debug("action2 = " + action);

        if (action.equalsIgnoreCase("delete")){
            int mealId = Integer.parseInt(request.getParameter("id"));
            mealRepository.delete(mealId);
            forward = LIST_MEAL;
            mealsTo = MealsUtil.filteredByStreams(mealRepository.readAll(), LocalTime.MIN, LocalTime.MAX,
                    MealsUtil.CALORIES_PER_DAY);
            request.setAttribute("mealsTo", mealsTo);
        } else if (action.equalsIgnoreCase("edit")){
            forward = INSERT_OR_EDIT;
            int mealId = Integer.parseInt(request.getParameter("id"));
            Meal meal = mealRepository.getMealById(mealId);
            request.setAttribute("meal", meal);
        } else if (action.equalsIgnoreCase("listMeal")){
            forward = LIST_MEAL;
            request.setAttribute("mealsTo", mealsTo);
        } else {
            forward = INSERT_OR_EDIT;
        }

        RequestDispatcher view = request.getRequestDispatcher(forward);
        view.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("redirect to add/edit");
        request.setCharacterEncoding("UTF8");
        Meal oldMeal = (Meal) request.getAttribute("meal");
        log.debug("oldMeal " + oldMeal);
        String id = request.getParameter("id");
        log.debug("get id " + id);
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        if (id == null || id.isEmpty()) {
            Meal meal = new Meal(mealRepository.readAll().size() + 1, dateTime, description, calories);
            mealRepository.create(meal);
        } else {
            Meal meal = new Meal(Integer.parseInt(id), dateTime, description, calories);
            mealRepository.update(meal);
        }
        List<MealTo> mealsTo = MealsUtil.filteredByStreams(mealRepository.readAll(), LocalTime.MIN, LocalTime.MAX,
                MealsUtil.CALORIES_PER_DAY);
        RequestDispatcher view = request.getRequestDispatcher(LIST_MEAL);
        request.setAttribute("mealsTo", mealsTo);
        view.forward(request, response);
    }
}


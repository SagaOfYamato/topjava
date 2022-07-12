package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.javawebinar.topjava.service.MealService;

import javax.servlet.http.HttpServletRequest;

@Controller
public class JspMealController {

    private static final Logger log = LoggerFactory.getLogger(JspMealController.class);

    @Autowired
    private MealService service;

    @GetMapping("/meals")
    public String getAll(Model model, HttpServletRequest request) {
        String action = request.getParameter("action");
        log.info("users");
        if (action == null) {
        int userId = SecurityUtil.authUserId();
        model.addAttribute("meals", service.getAll(userId));
        }
        return "meals.jsp";
    }
}

package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.web.user.AdminRestController;
import ru.javawebinar.topjava.web.user.ProfileRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import static org.slf4j.LoggerFactory.getLogger;
public class UserServlet extends HttpServlet {
    private static final Logger log = getLogger(UserServlet.class);
    ConfigurableApplicationContext appCtx;
    ProfileRestController profileRestController;
    AdminRestController adminUserController;

    @Override
    public void init() {
        appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        log.info("Bean definition names: {}",Arrays.toString(appCtx.getBeanDefinitionNames()));
        profileRestController = appCtx.getBean(ProfileRestController.class);
        profileRestController.create(new User(null, "userName=id1", "email@mail.ru", "password", Role.USER));
        adminUserController = appCtx.getBean(AdminRestController.class);
        adminUserController.create(new User(null, "userName", "email@mail.ru", "password", Role.ADMIN));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("forward to users");
        request.getRequestDispatcher("/users.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SecurityUtil.setUserId(Integer.parseInt(request.getParameter("userId")));
        response.sendRedirect("meals");
    }
}

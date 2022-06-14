package ru.javawebinar.topjava.web.user;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.User;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class AdminRestController extends AbstractUserController {

    private static final Logger log = getLogger(AdminRestController.class);

    @Override
    public List<User> getAll() {
        log.info("getAll - adminRestController");
        return super.getAll();
    }

    @Override
    public User get(int id) {
        return super.get(id);
    }

    @Override
    public User create(User user) {
        return super.create(user);
    }

    @Override
    public void delete(int id) {
        super.delete(id);
    }

    @Override
    public void update(User user, int id) {
        super.update(user, id);
    }

    @Override
    public User getByMail(String email) {
        return super.getByMail(email);
    }
}
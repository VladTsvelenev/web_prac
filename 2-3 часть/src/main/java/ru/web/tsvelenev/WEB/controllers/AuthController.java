package ru.web.tsvelenev.WEB.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.web.tsvelenev.WEB.DAO.UsersDAO;
import ru.web.tsvelenev.WEB.models.Users;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Optional;

@Controller
public class AuthController {

    private final UsersDAO usersDAO;
    private final ObjectMapper objectMapper;

    public AuthController(UsersDAO usersDAO, ObjectMapper objectMapper) {
        this.usersDAO = usersDAO;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/perform-login")
    public String performLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        try {
            Optional<Users> userOpt = usersDAO.findByEmail(username);

            if (userOpt.isPresent()) {
                Users user = userOpt.get();
                JsonNode userInfo = objectMapper.readTree(user.getUserInfo());

                // Проверяем пароль (в реальном приложении нужно хеширование)
                String storedPassword = userInfo.path("password").asText();

                if (password.equals(storedPassword)) {
                    session.setAttribute("username", username);
                    return "redirect:/";
                } else {
                    model.addAttribute("error", "Неверный пароль");
                }
            } else {
                model.addAttribute("error", "Пользователь не найден");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при входе: " + e.getMessage());
            e.printStackTrace();
        }
        return "login";
    }


    @GetMapping("/login")
    public String showLoginPage(@RequestParam(required = false) Boolean registered, Model model) {
        if (registered != null && registered) {
            model.addAttribute("message", "Регистрация прошла успешно! Теперь вы можете войти.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/perform-register")
    public String performRegister(@RequestParam String name,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam String confirmPassword,
                                  Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Пароли не совпадают");
            return "register";
        }

        // Проверяем, есть ли уже пользователь с таким email
        if (usersDAO.existsByEmail(email)) {
            model.addAttribute("error", "Пользователь с таким email уже существует");
            return "register";
        }

        try {
            // Создаем JSON для userInfo
            ObjectNode userInfo = objectMapper.createObjectNode();
            userInfo.put("name", name);
            userInfo.put("email", email);
            // В реальном приложении нужно хешировать пароль перед сохранением
            userInfo.put("password", password);

            Users newUser = new Users(userInfo.toString());
            usersDAO.save(newUser);

            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при регистрации: " + e.getMessage());
            return "register";
        }
    }

    @PostMapping("/logout")
    public String performLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
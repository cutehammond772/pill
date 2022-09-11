package me.cutehammond.pill.domain.pill.presentation.view;

import lombok.RequiredArgsConstructor;
import me.cutehammond.pill.domain.user.application.UserService;
import me.cutehammond.pill.domain.user.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final UserService userService;

    @GetMapping("/")
    public String index(Model model) {
        var user = userService.getCurrentUser();

        model.addAttribute("userName", user.map(User::getUserName).orElse(null));
        model.addAttribute("profile", user.map(User::getProfileUrl).orElse(null));
        model.addAttribute("userId", user.map(User::getUserId).orElse(null));
        return "index";
    }

}

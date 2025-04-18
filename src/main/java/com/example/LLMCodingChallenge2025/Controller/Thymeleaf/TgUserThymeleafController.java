package com.example.LLMCodingChallenge2025.Controller.Thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tg_user")
public class TgUserThymeleafController {

    @GetMapping("/login")
    public String loginPage() {
        return "tg_user/login";
    }
    @GetMapping("/register")
    public String registerPage() {
        return "tg_user/register";
    }
}

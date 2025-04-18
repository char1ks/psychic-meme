package com.example.LLMCodingChallenge2025.Controller.Thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BaseThymeleaf {

    @GetMapping("/main")
    public String main() {
        return "default/main";
    }
    @GetMapping("/analytics")
    public String analytics() {
        return "default/analytics";
    }
    @GetMapping("/smoke")
    public String smoke(){
        return "default/funny_smoke";
    }
}

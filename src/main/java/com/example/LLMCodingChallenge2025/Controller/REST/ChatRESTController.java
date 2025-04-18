package com.example.LLMCodingChallenge2025.Controller.REST;

import com.example.LLMCodingChallenge2025.Model.Chat.Chat;
import com.example.LLMCodingChallenge2025.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat/api")
public class ChatRESTController {

    @Autowired
    private ChatService operations;

    @GetMapping("/all")
    public List<Chat> findAll() {
        return operations.findAll();
    }
}

package com.example.LLMCodingChallenge2025.Service;

import com.example.LLMCodingChallenge2025.Model.Chat.Chat;
import com.example.LLMCodingChallenge2025.Repository.ChatRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ChatService {

    @Autowired
    private ChatRepository operations;

    public List<Chat> findAll() {
        return operations.findAll();
    }
}

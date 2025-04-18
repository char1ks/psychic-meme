package com.example.LLMCodingChallenge2025.Repository;

import com.example.LLMCodingChallenge2025.Model.Chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
}

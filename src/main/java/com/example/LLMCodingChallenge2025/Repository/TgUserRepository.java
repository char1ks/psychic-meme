package com.example.LLMCodingChallenge2025.Repository;

import com.example.LLMCodingChallenge2025.Model.Tg_user.TgUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TgUserRepository extends JpaRepository<TgUser, Integer> {
    TgUser findByUsername(String username);
}

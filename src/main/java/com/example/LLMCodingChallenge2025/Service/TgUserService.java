package com.example.LLMCodingChallenge2025.Service;

import com.example.LLMCodingChallenge2025.Model.Tg_user.TgUser;
import com.example.LLMCodingChallenge2025.Repository.TgUserRepository;
import com.example.LLMCodingChallenge2025.Security.TgUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TgUserService implements UserDetailsService {

    @Autowired
    private TgUserRepository operations;

    public TgUser saveTgUser(TgUser tgUser) {
        return operations.save(tgUser);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TgUser tgUser = operations.findByUsername(username);
        return new TgUserDetails(tgUser);
    }
}

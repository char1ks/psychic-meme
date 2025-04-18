package com.example.LLMCodingChallenge2025.Controller.REST;

import com.example.LLMCodingChallenge2025.Model.Tg_user.TgUser;
import com.example.LLMCodingChallenge2025.Service.TgUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tg_user/api")
public class TgUserRESTController {

    @Autowired
    private TgUserService operations;

    @PostMapping("/new")
    public ResponseEntity<Object> newUser(@RequestBody TgUser tgUser) {
        operations.saveTgUser(tgUser);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

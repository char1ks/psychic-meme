package com.example.LLMCodingChallenge2025.Config;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;

    public KafkaMessageListener(SimpMessagingTemplate messagingTemplate, MessageRepository messageRepository) {
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
    }

    @KafkaListener(topics = "messages", groupId = "telegram_listener_group")
    public void listen(String message) {
        String decodedMessage = StringEscapeUtils.unescapeJava(message);
        System.out.println("Получено сообщение: " + decodedMessage);
        // Отправляем сообщение через WebSocket
        messagingTemplate.convertAndSend("/topic/messages", decodedMessage);
    }
}

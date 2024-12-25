package com.example.stockmarket.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate template;

    @Operation(summary = "Send Notification", description = "Send Notification when balance card added")
    @ApiResponse(responseCode = "200", description = "Notification sent successfully.")
    @PostMapping("/sendNotification")
    public void sendNotification(@RequestParam("message") String message) {
        template.convertAndSend("/topic/notification", message);
    }

    public void sendNotificationNew(String username, String message) {
        template.convertAndSendToUser(username, "/topic/notifications", message);
    }
}

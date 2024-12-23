package com.example.stockmarket.controller;

import com.example.stockmarket.services.SseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
public class SseController {

    private final SseService sseService;

    @Autowired
    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    @GetMapping("/sse/connect")
    public SseEmitter connect() {
        return sseService.registerEmitter();
    }
}

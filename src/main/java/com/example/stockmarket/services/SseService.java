package com.example.stockmarket.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper;

    public SseService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public SseEmitter registerEmitter(){
        SseEmitter emitter = new SseEmitter(0L); //5min
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(throwable -> emitters.remove(emitter));
        return emitter;
    }

    public void sendEvent(String eventName, Object data) {
        try {
            String jsonData = objectMapper.writeValueAsString(data);
            System.out.println("SSE Event: " + eventName + ", Data: " + jsonData);

            for (SseEmitter emitter : emitters) {
                emitter.send(SseEmitter.event().name(eventName).data(jsonData));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
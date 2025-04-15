package org.ivanov.myshop.configuration.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionRegistry {
    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();

    public void registerSession(String username, String sessionId) {
        userSessionMap.put(username, sessionId);
    }

    public void unregisterSession(String username) {
        userSessionMap.remove(username);
    }

    public String getSessionId(String username) {
        return userSessionMap.get(username);
    }
}

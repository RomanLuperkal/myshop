package org.ivanov.myshop.configuration.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionRegistry {
    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();

    public boolean isSessionAllowed(String username, String newSessionId) {
        String existingSessionId = userSessionMap.get(username);
        return existingSessionId == null || existingSessionId.equals(newSessionId);
    }

    public void registerSession(String username, String sessionId) {
        userSessionMap.put(username, sessionId);
    }

    public void unregisterSession(String username, String sessionId) {
        userSessionMap.remove(username, sessionId);
    }
}

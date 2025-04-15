package org.ivanov.myshop.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

//@Component
public class WebSessionContextFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.getSession().flatMap(session -> {
            // Добавляем сессию в Reactor Context
            return chain.filter(exchange)
                    .contextWrite(context -> context.put("webSession", session));
        });
    }
}

package org.ivanov.myshop.cart.filter;

import lombok.AllArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
@AllArgsConstructor
public class MockRemoteAddressFilter implements WebFilter {
    private String host;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(decorate(exchange));
    }

    private ServerWebExchange decorate(ServerWebExchange exchange) {
        final ServerHttpRequest decorated = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public InetSocketAddress getRemoteAddress() {
                return new InetSocketAddress(host, 80);
            }
        };

        return new ServerWebExchangeDecorator(exchange) {
            @Override
            public ServerHttpRequest getRequest() {
                return decorated;
            }
        };
    }
}

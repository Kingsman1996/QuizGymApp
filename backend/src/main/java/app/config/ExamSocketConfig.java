package app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class ExamSocketConfig implements WebSocketConfigurer {
    private final ExamSocketHandler examSocketHandler;
    private final KickWebSocketHandler kickWebSocketHandler;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(examSocketHandler, "/ws/rooms")
                .setAllowedOrigins(frontendUrl);
        registry.addHandler(kickWebSocketHandler, "/ws/kick")
                .setAllowedOrigins(frontendUrl);
    }
}

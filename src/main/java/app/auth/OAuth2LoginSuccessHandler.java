package app.auth;

import app.entity.User;
import app.exception.AuthException;
import app.service.JwtService;
import app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserService userService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");
        User user = userService.findByEmail(email);
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        String accessTokenEncoded = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        String refreshTokenEncoded = URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);
        String redirectUrl = frontendUrl + "/login" + "?accessToken=" + accessTokenEncoded + "&refreshToken=" + refreshTokenEncoded;
        try {
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            throw new AuthException(e.getMessage());
        }
    }
}

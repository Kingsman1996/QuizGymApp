package app.service;

import app.dto.user.ChangePasswordRequest;
import app.dto.user.LoginRequest;
import app.dto.user.RecoverPasswordRequest;
import app.dto.user.RegisterRequest;
import app.entity.Token;
import app.entity.User;
import app.exception.*;
import app.mapper.RegisterMapper;
import app.repository.TokenRepository;
import app.repository.UserRepository;
import app.util.MessageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RegisterMapper registerMapper;
    private final MessageHelper messageHelper;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

    @Value("${upload.directory}")
    private String uploadDirectory;

    @Value("${upload.url.prefix}")
    private String urlPrefix;

    public void confirmEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("user.not.found"));
        user.setActive(true);
        userRepository.save(user);
    }

    public List<User> findAllExceptAdminSortByCreateAt() {
        return userRepository.findByIsAdminFalseOrderByCreateAtAsc();
    }

    public List<User> searchFollowNameAndEmail(String keyName, String keyEmail) {
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyName, keyEmail);
    }

    public String blockUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(messageHelper.get("user.not.found")));
        user.setActive(false);
        userRepository.save(user);

        return user.getEmail();
    }

    @Transactional
    public void recoverPassword(RecoverPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException(messageHelper.get("user.not.found")));
        if (request.getPassword().equals(user.getPassword())) {
            throw new DuplicateException(messageHelper.get("password.duplicate"));
        }
        Token foundToken = tokenRepository.findByCode(request.getCode());
        if (!foundToken.getUser().getId().equals(user.getId())) {
            throw new NotMatchException(messageHelper.get("token.invalid"));
        }
        user.setPassword(request.getPassword());
        userRepository.save(user);
        tokenRepository.deleteAllByUser(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public String register(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        String confirmPassword = registerRequest.getConfirmPassword();

        if (!password.equals(confirmPassword)) {
            throw new NotMatchException(messageHelper.get("password.confirm.not.match"));
        }

        if (username == null || username.trim().isEmpty()) {
            registerRequest.setUsername(email);
        }

        if (existsByEmail(email)) {
            throw new DuplicateException(messageHelper.get("email.exists"));
        }

        User user = registerMapper.toEntity(registerRequest);
        user.setActive(false);
        user.setCreateAt(LocalDateTime.now());
        userRepository.save(user);
        return email;
    }

    public Map<String, String> login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new AuthException(messageHelper.get("email.not.exist"));
        }

        User user = userOptional.get();
        if (!user.isActive()) {
            throw new NotActiveException(messageHelper.get("not_active_account"));
        }

        if (!password.equals(user.getPassword())) {
            throw new AuthException(messageHelper.get("password.incorrect"));
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        return Map.of("accessToken", jwtService.generateToken(user),
                "refreshToken", jwtService.generateRefreshToken(user.getEmail()));
    }

    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findByEmail(changePasswordRequest.getEmail())
                .orElseThrow(() -> new NotFoundException(messageHelper.get("user.not.found")));

        if (!changePasswordRequest.getOldPassword().equals(user.getPassword())) {
            throw new NotMatchException(messageHelper.get("password.old.not.match"));
        }

        if (changePasswordRequest.getNewPassword().equals(user.getPassword())) {
            throw new SameAsOldException(messageHelper.get("password.new.match.old"));
        }

        user.setPassword(changePasswordRequest.getNewPassword());
        userRepository.save(user);
    }

    public void editProfile(String email, String username, MultipartFile avatarFile) {
        User user = findByEmail(email);

        if (username != null && !username.trim().isEmpty()) {
            user.setUsername(username);
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String fileName = avatarFile.getOriginalFilename();
                if (fileName == null || fileName.trim().isEmpty()) {
                    throw new UploadException(messageHelper.get("file.name.invalid"));
                }
                Path uploadPath = Paths.get(uploadDirectory, fileName);
                if (Files.exists(uploadPath)) {
                    String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
                    String fileExt = fileName.substring(fileName.lastIndexOf('.'));
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                    fileName = fileNameWithoutExt + "_" + timestamp + fileExt;
                    uploadPath = Paths.get(uploadDirectory, fileName);
                }
                Files.createDirectories(uploadPath.getParent());
                Files.copy(avatarFile.getInputStream(), uploadPath);
                user.setAvatar(urlPrefix + fileName);
            } catch (IOException e) {
                throw new UploadException(messageHelper.get("file.upload.error") + ": " + e.getMessage());
            }
        }

        userRepository.save(user);
    }

    public Map<String, Object> getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(messageHelper.get("user.not.found")));
        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("avatar", user.getAvatar());
        response.put("createAt", user.getCreateAt());
        response.put("active", user.isActive());
        response.put("lastLogin", user.getLastLogin());
        return response;
    }

    public void unblockUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(messageHelper.get("user.not.found")));
        user.setActive(true);
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(messageHelper.get("user.not.found"))
        );
    }

    public User findInAuth() {
        return findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

//    public Map<String, String> refreshToken(String refreshToken) {
//        Map<String, String> response = new HashMap<>();
//        try {
//            String newAccessToken = tokenService.refreshAccessToken(refreshToken);
//            response.put("accessToken", newAccessToken);
//        } catch (Exception e) {
//            response.put("error", messageHelper.get("refresh.token.invalid"));
//        }
//        return response;
//    }

}
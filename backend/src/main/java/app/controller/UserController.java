package app.controller;

import app.config.KickWebSocketHandler;
import app.dto.user.*;
import app.exception.DuplicateException;
import app.exception.NotFoundException;
import app.service.UserService;
import app.util.BindingHandler;
import app.util.MessageHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final MessageHelper messageHelper;
    private final KickWebSocketHandler kickUser;

    @PatchMapping("/confirm")
    public ResponseEntity<?> confirmEmail(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        userService.confirmEmail(email);
        return ResponseEntity.ok("Xác nhận thành công");
    }

    @PostMapping("/register")
    public ResponseEntity<?> processRegister(@Valid @RequestBody RegisterRequest registerRequest,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(BindingHandler.getErrorMessages(bindingResult));
        }
        return ResponseEntity.ok().body(userService.register(registerRequest));
    }

    @PatchMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().body(userService.login(loginRequest));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.findAllExceptAdminSortByCreateAt());
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(required = false) String keyName,
                                    @RequestParam(required = false) String keyEmail) {
        return ResponseEntity.ok(userService.searchFollowNameAndEmail(keyName, keyEmail));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{userId}/block")
    public ResponseEntity<?> blockUser(@PathVariable long userId) throws IOException {
        String email = userService.blockUser(userId);
        kickUser.kickUser(email);
        return ResponseEntity.status(HttpStatus.OK).body(messageHelper.get("block.user.success"));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{userId}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable long userId) {
        userService.unblockUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(messageHelper.get("unblock.user.success"));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok(messageHelper.get("update.success"));
    }

    @PatchMapping("/edit")
    public ResponseEntity<?> editProfile(
            @RequestParam("email") String email,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile) {
        userService.editProfile(email, username, avatarFile);
        return ResponseEntity.ok(messageHelper.get("update.success"));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam("email") String email) {
        return ResponseEntity.ok().body(userService.getProfile(email));
    }

    @PatchMapping("/recover-password")
    public ResponseEntity<?> recoverPassword(@Valid @RequestBody RecoverPasswordRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(BindingHandler.getErrorMessages(bindingResult));
        }
        userService.recoverPassword(request);
        return ResponseEntity.ok().body(messageHelper.get("reset.password.success"));
    }

//    @PostMapping("/refresh-token")
//    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
//        String refreshToken = request.get("refreshToken");
//        Map<String, String> response = userService.refreshToken(refreshToken);
//        if (response.containsKey("error")) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response.get("error"));
//        }
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/check/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable String email) {
        if (userService.existsByEmail(email)) {
            return ResponseEntity.ok().build();
        }
        throw new NotFoundException(messageHelper.get("user.not.found"));
    }


    @GetMapping("/avatar")
    public ResponseEntity<?> getAvatar() {
        return ResponseEntity.ok().body(userService.findInAuth().getAvatar());
    }
}
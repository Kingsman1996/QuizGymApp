package app.service;

import app.dto.email.EmailRequest;
import app.dto.email.SendAnnounceRequest;
import app.dto.email.SendCodeRequest;
import app.entity.Token;
import app.entity.User;
import app.exception.EmailException;
import app.exception.NotFoundException;
import app.repository.TokenRepository;
import app.repository.UserRepository;
import app.util.MessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final TokenRepository tokenRepository;
    private final MessageHelper messageHelper;

    public void sendToEmail(EmailRequest request) {
        try {
            User user = userRepository.findByEmail(request.getTo())
                    .orElseThrow(() -> new NotFoundException(messageHelper.get("user.not.found")));
            send(request.getTo(), request.getSubject(), request.getHtml());
        } catch (MessagingException e) {
            throw new EmailException(messageHelper.get("send.email.fail"));
        }
    }

    public void sendCode(SendCodeRequest request) {
        try {
            send(request.getTo(), request.getSubject(), request.getHtml());
            Token token = new Token();
            token.setCode(request.getToken());
            User foundUser = userRepository.findByEmail(request.getTo()).orElseThrow(
                    () -> new NotFoundException(messageHelper.get("user.not.found")));
            token.setUser(foundUser);
            tokenRepository.save(token);
        } catch (Exception e) {
            throw new EmailException("Lỗi khi gửi Code: " + e.getMessage());
        }
    }

    public void sendAnnounce(SendAnnounceRequest request) {
        try {
            userRepository.findByEmail(request.getTo()).orElseThrow(() -> new NotFoundException(messageHelper.get("user.not.found")));
            send(request.getTo(), request.getSubject(), request.getHtml());
        } catch (Exception e) {
            throw new EmailException("Lỗi khi gửi thông báo: " + e.getMessage());
        }
    }

    private void send(String to, String subject, String html) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        mailSender.send(mimeMessage);
    }
}

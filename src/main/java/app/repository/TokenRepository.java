package app.repository;

import app.entity.Token;
import app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findByCode(String code);
    void deleteAllByUser(User user);
}

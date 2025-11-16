package cm.bogne_stanley.money_flow.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cm.bogne_stanley.money_flow.data.entity.RefreshToken;
import cm.bogne_stanley.money_flow.data.entity.User;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findByUser(User user);
}

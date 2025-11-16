package cm.bogne_stanley.money_flow.data.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cm.bogne_stanley.money_flow.data.entity.ActivationCode;
import cm.bogne_stanley.money_flow.data.entity.User;

public interface ActivationCodeRepository extends CrudRepository<ActivationCode, Long> {
    Optional<ActivationCode> findByCode(String code);
    Optional<ActivationCode> findByUser(User user);
}

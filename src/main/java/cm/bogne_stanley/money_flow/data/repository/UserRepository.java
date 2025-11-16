package cm.bogne_stanley.money_flow.data.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cm.bogne_stanley.money_flow.data.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

package cm.bogne_stanley.money_flow.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import cm.bogne_stanley.money_flow.data.entity.Category;
import cm.bogne_stanley.money_flow.data.entity.User;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameAndUser(String name, User user);

    Optional<Category> findByIdAndUser(Long id, User user);

    List<Category> findByUserOrderByCreatedAtDesc(User user);

    Page<Category> findByUser(User user, Pageable pageable);
}

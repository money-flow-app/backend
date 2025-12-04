package cm.bogne_stanley.money_flow.data.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import cm.bogne_stanley.money_flow.data.entity.Expense;
import cm.bogne_stanley.money_flow.data.entity.User;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Optional<Expense> findByIdAndUser(Long id, User user);
    Page<Expense> findAllByUserAndCreatedAtBetweenOrderByCreatedAtDesc(User user, Instant startDate, Instant endDate, Pageable pageable);
}

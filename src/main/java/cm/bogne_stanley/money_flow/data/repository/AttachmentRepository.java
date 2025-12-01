package cm.bogne_stanley.money_flow.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cm.bogne_stanley.money_flow.data.entity.Attachment;
import cm.bogne_stanley.money_flow.data.entity.Expense;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByExpense(Expense expense);
    void deleteByExpense(Expense expense);
    Optional<Attachment> findByIdAndExpense(Long id, Expense expense);
}

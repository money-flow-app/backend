package cm.bogne_stanley.money_flow.data.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cm.bogne_stanley.money_flow.data.entity.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    Optional<Category> findByName(String name);
}

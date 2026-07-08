package com.my.financetracker.repository;

import com.my.financetracker.entity.Category;
import com.my.financetracker.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserIdAndType(Long userId, TransactionType type);
    List<Category> findUserById(Long userId);
    Category findByNameIgnoreCase(String name);
    Optional<Category> findByNameIgnoreCaseAndIdNot(String name, Long id);
}

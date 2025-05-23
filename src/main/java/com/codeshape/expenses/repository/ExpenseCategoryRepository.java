package com.codeshape.expenses.repository;

import com.codeshape.expenses.model.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    Optional<ExpenseCategory> findByName(String name);
}

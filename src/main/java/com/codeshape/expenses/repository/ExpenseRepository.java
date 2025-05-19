package com.codeshape.expenses.repository;

import com.codeshape.expenses.model.Expense;
import com.codeshape.expenses.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import com.codeshape.expenses.model.User;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserId(Long userId);

    List<Expense> findByUser(User user);
    List<Expense> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
    List<Expense> findByCategory(Expense.Category category);
    List<Expense> findByUserAndCategory(User user, Expense.Category category);

}
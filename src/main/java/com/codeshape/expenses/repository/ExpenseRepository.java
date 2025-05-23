package com.codeshape.expenses.repository;

import com.codeshape.expenses.model.Expense;
import com.codeshape.expenses.model.ExpenseCategory;
import com.codeshape.expenses.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT COUNT(e) FROM Expense e")
    long countAllExpenses();
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e")
    double sumAllExpenseAmounts();
    List<Expense> findByUserId(Long userId);
    List<Expense> findByUser(User user);
    List<Expense> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
    List<Expense> findByCategory(ExpenseCategory category);
    List<Expense> findByUserAndCategory(User user, ExpenseCategory category);
    List<Expense> findByUserAndDescriptionContainingIgnoreCase(User user, String keyword);
    Page<Expense> findByUser(User user, Pageable pageable);
}
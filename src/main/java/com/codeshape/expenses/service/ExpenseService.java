package com.codeshape.expenses.service;

import com.codeshape.expenses.model.Expense;
import com.codeshape.expenses.model.User;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    Expense saveExpense(Expense expense, Long userId);
    List<Expense> getExpensesByUserId(Long userId);
    Expense updateExpense(Long expenseId, Expense updatedExpense);
    void deleteExpense(Long expenseId);
    List<Expense> getExpensesByUser(User user);
    Expense createExpense(User user, Expense expense);
    List<Expense> getExpensesByUserAndDateRange(User user, LocalDate start, LocalDate end);
    List<Expense> getExpensesByCategory(Expense.Category category);
    List<Expense> getExpensesByUserAndCategory(User user, Expense.Category category);

}
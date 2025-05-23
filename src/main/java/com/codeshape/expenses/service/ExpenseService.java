package com.codeshape.expenses.service;

import com.codeshape.expenses.model.Expense;
import com.codeshape.expenses.model.User;
import com.codeshape.expenses.model.ExpenseCategory;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExpenseService {
    Expense saveExpense(Expense expense, Long userId);
    List<Expense> getExpensesByUserId(Long userId);
    Expense updateExpense(Long expenseId, Expense updatedExpense);
    void deleteExpense(Long expenseId);
    List<Expense> getExpensesByUser(User user);
    Page<Expense> getExpensesByUser(User user, Pageable pageable);
    Expense createExpense(User user, Expense expense);
    List<Expense> getExpensesByUserAndDateRange(User user, LocalDate start, LocalDate end);
    List<Expense> getExpensesByCategory(ExpenseCategory category);
    List<Expense> getExpensesByUserAndCategory(User user, ExpenseCategory category);
    Double getTotalExpenseAmountByUser(User user);
    Map<String, Double> getMonthlyExpenseSummaryByUser(User user);
    Map<String, Double> getYearlySummaryByCategory(User user, int year);
    Map<String, Double> getMonthlySummaryByCategory(User user, int year, int month);
    List<String> getInvoiceFilenamesByUser(User user);
    List<Expense> searchExpensesByDescription(User user, String keyword);
    String storeFile(MultipartFile file);
    org.springframework.core.io.Resource loadInvoiceFileAsResource(String filename);
    Expense getExpenseById(Long id);
    List<Expense> searchExpensesByKeyword(User user, String keyword);
    void exportExpensesToCsv(User user, HttpServletResponse response) throws IOException;
    void exportExpensesToExcel(User user, HttpServletResponse response) throws IOException;
    Page<Expense> getExpensesByUserWithPaginationAndSorting(User user, int pageNo, int pageSize, String sortBy, String sortDir);


}

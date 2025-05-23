package com.codeshape.expenses.service;

import com.codeshape.expenses.dto.AdminDashboardStatsDTO;
import com.codeshape.expenses.repository.UserRepository;
import com.codeshape.expenses.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    public AdminDashboardService(UserRepository userRepository, ExpenseRepository expenseRepository) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
    }

    public AdminDashboardStatsDTO getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalExpenses = expenseRepository.countAllExpenses();
        double totalExpenseAmount = expenseRepository.sumAllExpenseAmounts();

        return new AdminDashboardStatsDTO(totalUsers, totalExpenses, totalExpenseAmount);
    }
}

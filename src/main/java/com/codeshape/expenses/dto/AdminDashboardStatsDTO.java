package com.codeshape.expenses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminDashboardStatsDTO {
    private long totalUsers;
    private long totalExpenses;
    private double totalExpenseAmount;
}

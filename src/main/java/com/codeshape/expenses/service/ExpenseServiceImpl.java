package com.codeshape.expenses.service;

import com.codeshape.expenses.model.Expense;
import com.codeshape.expenses.model.User;
import com.codeshape.expenses.model.ExpenseCategory;
import com.codeshape.expenses.repository.ExpenseRepository;
import com.codeshape.expenses.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Expense saveExpense(Expense expense, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    @Override
    public List<Expense> getExpensesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return expenseRepository.findByUser(user);
    }
    @Override
    public Page<Expense> getExpensesByUser(User user, Pageable pageable) {
        return expenseRepository.findByUser(user, pageable);
    }

    @Override
    public Expense updateExpense(Long expenseId, Expense updatedExpense) {
        Expense existingExpense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found with id: " + expenseId));
        existingExpense.setAmount(updatedExpense.getAmount());
        existingExpense.setCategory(updatedExpense.getCategory());
        existingExpense.setDate(updatedExpense.getDate());
        existingExpense.setDescription(updatedExpense.getDescription());
        existingExpense.setFileName(updatedExpense.getFileName());
        return expenseRepository.save(existingExpense);
    }

    @Override
    public void deleteExpense(Long expenseId) {
        Expense existingExpense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found with id: " + expenseId));
        expenseRepository.delete(existingExpense);
    }

    @Override
    public List<Expense> getExpensesByUser(User user){
        return expenseRepository.findByUser(user);
    }

    @Override
    public Expense createExpense(User user, Expense expense) {
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    @Override
    public List<Expense> getExpensesByUserAndDateRange(User user, LocalDate start, LocalDate end) {
        return expenseRepository.findByUserAndDateBetween(user, start, end);
    }

    @Override
    public List<Expense> getExpensesByCategory(ExpenseCategory category) {
        return expenseRepository.findByCategory(category);
    }

    @Override
    public List<Expense> getExpensesByUserAndCategory(User user, ExpenseCategory category) {
        return expenseRepository.findByUserAndCategory(user, category);
    }

    @Override
    public Double getTotalExpenseAmountByUser(User user) {
        List<Expense> expenses = expenseRepository.findByUser(user);
        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }


    @Override
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        String uploadDir = "uploads/";
        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            File dest = new File(uploadDir + uniqueFileName);
            file.transferTo(dest);
            return uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Double> getMonthlyExpenseSummaryByUser(User user) {
        List<Expense> expenses = expenseRepository.findByUser(user);
        return expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDate().getYear() + "-" + String.format("%02d", e.getDate().getMonthValue()),
                        TreeMap::new, // Keeps keys in order (Jan–Dec)
                        Collectors.summingDouble(Expense::getAmount)
                ));
    }
    @Override
    public Map<String, Double> getYearlySummaryByCategory(User user, int year) {
        List<Expense> expenses = expenseRepository.findByUser(user);
        Map<String, Double> summary = new TreeMap<>();

        for (Expense expense : expenses) {
            if (expense.getDate().getYear() == year) {
                String categoryName = expense.getCategory().getName();
                summary.put(categoryName,
                        summary.getOrDefault(categoryName, 0.0) + expense.getAmount());
            }
        }
        return summary;
    }
    @Override
    public Map<String, Double> getMonthlySummaryByCategory(User user, int year, int month) {
        List<Expense> expenses = expenseRepository.findByUser(user);
        Map<String, Double> summary = new TreeMap<>();

        for (Expense expense : expenses) {
            if (expense.getDate().getYear() == year && expense.getDate().getMonthValue() == month) {
                String categoryName = expense.getCategory().getName();
                summary.put(categoryName,
                        summary.getOrDefault(categoryName, 0.0) + expense.getAmount());
            }
        }
        return summary;
    }
    @Override
    public List<String> getInvoiceFilenamesByUser(User user) {
        List<Expense> expenses = expenseRepository.findByUser(user);
        List<String> filenames = new ArrayList<>();

        for (Expense expense : expenses) {
            if (expense.getFileName() != null) {
                filenames.add(expense.getFileName());
            }
        }
        return filenames;
    }

    @Override
    public List<Expense> searchExpensesByDescription(User user, String keyword) {
        return expenseRepository.findByUserAndDescriptionContainingIgnoreCase(user, keyword);
    }
    @Override
    public org.springframework.core.io.Resource loadInvoiceFileAsResource(String filename) {
        try {
            Path filePath = Paths.get("uploads").resolve(filename).normalize();
            org.springframework.core.io.Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not readable: " + filename, e);
        }
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
    }

    @Override
    public List<Expense> searchExpensesByKeyword(User user, String keyword) {
        return expenseRepository.findByUserAndDescriptionContainingIgnoreCase(user, keyword);
    }
    @Override
    public void exportExpensesToCsv(User user, HttpServletResponse response) throws IOException {
        List<Expense> expenses = expenseRepository.findByUser(user);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"expenses.csv\"");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Date,Amount,Category,Description");

        for (Expense expense : expenses) {
            writer.printf("%d,%s,%.2f,%s,%s%n",
                    expense.getId(),
                    expense.getDate(),
                    expense.getAmount(),
                    expense.getCategory().getName(),
                    expense.getDescription().replace(",", " ")); // avoid breaking CSV
        }

        writer.flush();
        writer.close();
    }

    @Override
    public void exportExpensesToExcel(User user, HttpServletResponse response) throws IOException {
        List<Expense> expenses = expenseRepository.findByUser(user);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Expenses");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Date");
        header.createCell(2).setCellValue("Amount");
        header.createCell(3).setCellValue("Category");
        header.createCell(4).setCellValue("Description");

        int rowIdx = 1;
        for (Expense expense : expenses) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(expense.getId());
            row.createCell(1).setCellValue(expense.getDate().toString());
            row.createCell(2).setCellValue(expense.getAmount());
            row.createCell(3).setCellValue(expense.getCategory().getName());
            row.createCell(4).setCellValue(expense.getDescription());
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"expenses.xlsx\"");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @Override
    public Page<Expense> getExpensesByUserWithPaginationAndSorting(User user, int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return expenseRepository.findByUser(user, pageable);
    }


}

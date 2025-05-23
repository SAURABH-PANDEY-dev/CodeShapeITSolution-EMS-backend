package com.codeshape.expenses.controller;

import com.codeshape.expenses.model.Expense;
import com.codeshape.expenses.model.ExpenseCategory;
import com.codeshape.expenses.model.User;
import com.codeshape.expenses.service.ExpenseCategoryService;
import com.codeshape.expenses.service.ExpenseService;
import com.codeshape.expenses.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.net.MalformedURLException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @Autowired
    private ExpenseCategoryService expenseCategoryService;

    // Add this helper method
//    private boolean isRequestingOwnData(User authenticatedUser, Long requestedUserId) {
//        return authenticatedUser != null && authenticatedUser.getId().equals(requestedUserId);
//    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Expense>> getExpensesByUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        List<Expense> expenses = expenseService.getExpensesByUser(user);
        return ResponseEntity.ok(expenses);
    }

    @PostMapping(value = "/user/{userId}/multipart", consumes = "multipart/form-data")
    public ResponseEntity<Expense> createExpenseMultipart(
            @PathVariable Long userId,
            @RequestPart("expense") Expense expense,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (expense.getCategory() == null || expense.getCategory().getId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        ExpenseCategory category = expenseCategoryService.getCategoryById(expense.getCategory().getId());
        if (category == null) {
            return ResponseEntity.badRequest().body(null);
        }

        String fileName = expenseService.storeFile(file);
        expense.setFileName(fileName);
        expense.setCategory(category);

        Expense created = expenseService.createExpense(user, expense);
        return ResponseEntity.ok(created);
    }


    @PostMapping("/user/{userId}")
    public ResponseEntity<Expense> createExpense(@PathVariable Long userId, @RequestBody Expense expense) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (expense.getCategory() == null || expense.getCategory().getId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        ExpenseCategory category = expenseCategoryService.getCategoryById(expense.getCategory().getId());
        if (category == null) {
            return ResponseEntity.badRequest().body(null);
        }

        expense.setCategory(category);
        Expense created = expenseService.createExpense(user, expense);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long expenseId, @RequestBody Expense updatedExpense) {
        Expense expense = expenseService.updateExpense(expenseId, updatedExpense);
        return ResponseEntity.ok(expense);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.ok("Expense deleted successfully.");
    }

    @GetMapping("/user/{userId}/filter-by-date")
    public ResponseEntity<List<Expense>> filterExpensesByDate(
            @PathVariable Long userId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<Expense> filtered = expenseService.getExpensesByUserAndDateRange(user, startDate, endDate);
        return ResponseEntity.ok(filtered);
    }

    @GetMapping("/user/{userId}/filter-by-category")
    public ResponseEntity<List<Expense>> filterExpensesByCategory(
            @PathVariable Long userId,
            @RequestParam("categoryId") Long categoryId) {

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        ExpenseCategory category = expenseCategoryService.getCategoryById(categoryId);
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Expense> filtered = expenseService.getExpensesByUserAndCategory(user, category);
        return ResponseEntity.ok(filtered);
    }

    @GetMapping("/user/{userId}/total")
    public ResponseEntity<Double> getTotalExpenses(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        Double total = expenseService.getTotalExpenseAmountByUser(user);
        return ResponseEntity.ok(total);
    }


    @PutMapping(value = "/{expenseId}/multipart", consumes = "multipart/form-data")
    public ResponseEntity<Expense> updateExpenseMultipart(
            @PathVariable Long expenseId,
            @RequestPart("expense") Expense updatedExpense,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        if (updatedExpense.getCategory() != null && updatedExpense.getCategory().getId() != null) {
            ExpenseCategory category = expenseCategoryService.getCategoryById(updatedExpense.getCategory().getId());
            if (category == null) {
                return ResponseEntity.badRequest().body(null);
            }
            updatedExpense.setCategory(category);
        }

        if (file != null && !file.isEmpty()) {
            String fileName = expenseService.storeFile(file);
            updatedExpense.setFileName(fileName);
        }

        Expense expense = expenseService.updateExpense(expenseId, updatedExpense);
        return ResponseEntity.ok(expense);
    }

    @GetMapping("/user/{userId}/monthly-summary")
    public ResponseEntity<Map<String, Double>> getMonthlySummary(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Double> summary = expenseService.getMonthlyExpenseSummaryByUser(user);
        return ResponseEntity.ok(summary);
    }
    @GetMapping("/user/{userId}/yearly-summary")
    public ResponseEntity<Map<String, Double>> getYearlySummaryByCategory(
            @PathVariable Long userId,
            @RequestParam int year) {

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Double> summary = expenseService.getYearlySummaryByCategory(user, year);
        return ResponseEntity.ok(summary);
    }
    @GetMapping("/user/{userId}/monthly-summary-by-category")
    public ResponseEntity<Map<String, Double>> getMonthlySummaryByCategory(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month) {

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Double> summary = expenseService.getMonthlySummaryByCategory(user, year, month);
        return ResponseEntity.ok(summary);
    }
    @GetMapping("/user/{userId}/invoices")
    public ResponseEntity<List<String>> getUserInvoiceFilenames(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<String> filenames = expenseService.getInvoiceFilenamesByUser(user);
        return ResponseEntity.ok(filenames);
    }

    @GetMapping("/user/{userId}/search")
    public ResponseEntity<List<Expense>> searchExpensesByDescription(
            @PathVariable Long userId,
            @RequestParam("keyword") String keyword) {

        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<Expense> results = expenseService.searchExpensesByDescription(user, keyword);
        return ResponseEntity.ok(results);
    }
    @GetMapping("/invoices/{filename:.+}")
    public ResponseEntity<Resource> downloadInvoiceFile(@PathVariable String filename) {
        Resource resource = expenseService.loadInvoiceFileAsResource(filename);

        String contentType;
        try {
            contentType = Files.probeContentType(resource.getFile().toPath());
        } catch (IOException ex) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/user/invoices")
    public ResponseEntity<List<String>> getUserInvoiceFilenames(@AuthenticationPrincipal User user) {
        List<String> filenames = expenseService.getInvoiceFilenamesByUser(user);
        return ResponseEntity.ok(filenames);
    }

    // Download/View invoice file
    @GetMapping("/download-invoice/{expenseId}")
    public ResponseEntity<Resource> downloadInvoice(@PathVariable Long expenseId) {
        Expense expense = expenseService.getExpenseById(expenseId);
        String filename = expense.getFileName();

        if (filename == null || filename.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Path filePath = Paths.get("uploads").resolve(filename).normalize();
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            String contentType = Files.probeContentType(filePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/search")
    public ResponseEntity<List<Expense>> searchExpenses(@RequestParam String keyword,
                                                        Principal principal) {
        User user = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Expense> results = expenseService.searchExpensesByKeyword(user, keyword);
        return ResponseEntity.ok(results);
    }
    @GetMapping("/export")
    public void exportExpensesToCsv(Principal principal, HttpServletResponse response) throws IOException {
        User user = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        expenseService.exportExpensesToCsv(user, response);
    }

    @GetMapping("/export/excel")
    public void exportExpensesToExcel(Principal principal, HttpServletResponse response) throws IOException {
        User user = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        expenseService.exportExpensesToExcel(user, response);
    }

    @GetMapping
    public ResponseEntity<Page<Expense>> getExpenses(
            Principal principal,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        User user = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Page<Expense> expensesPage = expenseService.getExpensesByUserWithPaginationAndSorting(user, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(expensesPage);
    }
    @GetMapping("/categories")
    public ResponseEntity<List<ExpenseCategory>> getAllCategories() {
        List<ExpenseCategory> categories = expenseCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}
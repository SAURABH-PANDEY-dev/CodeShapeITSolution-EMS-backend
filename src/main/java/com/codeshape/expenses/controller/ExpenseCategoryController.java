package com.codeshape.expenses.controller;

import com.codeshape.expenses.model.ExpenseCategory;
import com.codeshape.expenses.service.ExpenseCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class ExpenseCategoryController {

    private final ExpenseCategoryService categoryService;

    public ExpenseCategoryController(ExpenseCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<ExpenseCategory>> getAll() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<ExpenseCategory> create(@RequestBody ExpenseCategory category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}

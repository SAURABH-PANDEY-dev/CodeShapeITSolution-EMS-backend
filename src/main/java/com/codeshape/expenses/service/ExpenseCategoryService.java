package com.codeshape.expenses.service;

import com.codeshape.expenses.model.ExpenseCategory;
import com.codeshape.expenses.repository.ExpenseCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseCategoryService {

    private final ExpenseCategoryRepository categoryRepository;

    public ExpenseCategoryService(ExpenseCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<ExpenseCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public ExpenseCategory createCategory(ExpenseCategory category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public ExpenseCategory getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

}

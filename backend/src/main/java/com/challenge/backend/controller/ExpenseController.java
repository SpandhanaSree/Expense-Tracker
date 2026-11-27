
package com.challenge.backend.controller;

import com.challenge.backend.model.Expense;
import com.challenge.backend.model.ExpenseCategory;
import com.challenge.backend.repository.ExpenseRepository;
import com.challenge.backend.service.SummaryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final SummaryService summaryService;

    public ExpenseController(
            ExpenseRepository expenseRepository,
            SummaryService summaryService) {
        this.expenseRepository = expenseRepository;
        this.summaryService = summaryService;
    }

    @GetMapping("/expenses")
    public List<Expense> getExpenses() {
        return expenseRepository.findAll().stream()
                .sorted(Comparator.comparing(
                        Expense::getDate,
                        Comparator.reverseOrder()))
                .toList();
    }

    @PostMapping("/expenses")
    public ResponseEntity<?> addExpense(
            @RequestBody Map<String, Object> request) {

        Map<String, String> errors = new LinkedHashMap<>();

        String title = Objects.toString(request.get("title"), "").trim();
        String amountValue = Objects.toString(request.get("amount"), "");
        String categoryValue = Objects.toString(request.get("category"), "");
        String dateValue = Objects.toString(request.get("date"), "");

        if (title.isBlank()) {
            errors.put("title", "Title is required");
        }

        BigDecimal amount = null;

        try {
            amount = new BigDecimal(amountValue);

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                errors.put("amount", "Must be greater than 0");
            } else if (amount.scale() > 2) {
                errors.put("amount", "Maximum 2 decimal places allowed");
            }
        } catch (Exception e) {
            errors.put("amount", "Enter a valid amount");
        }

        ExpenseCategory category = null;

        try {
            category = ExpenseCategory.valueOf(categoryValue);
        } catch (Exception e) {
            errors.put("category", "Invalid category");
        }

        LocalDate date = null;

        try {
            date = LocalDate.parse(dateValue);

            if (date.isAfter(LocalDate.now())) {
                errors.put("date", "Future dates are not allowed");
            }
        } catch (Exception e) {
            errors.put("date", "Enter a valid date (YYYY-MM-DD)");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("errors", errors));
        }

        Expense expense = new Expense(
                UUID.randomUUID().toString(),
                title,
                amount.setScale(2, RoundingMode.HALF_UP).doubleValue(),
                category,
                date
        );

        expenseRepository.save(expense);

        return ResponseEntity.status(HttpStatus.CREATED).body(expense);
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable String id) {

        boolean deleted = expenseRepository.deleteById(id);

        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Expense not found"));
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return summaryService.getSummary();
    }
}

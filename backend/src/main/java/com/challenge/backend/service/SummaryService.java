
package com.challenge.backend.service;

import com.challenge.backend.model.Expense;
import com.challenge.backend.model.ExpenseCategory;
import com.challenge.backend.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class SummaryService {

    private final ExpenseRepository expenseRepository;

    public SummaryService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Map<String, Object> getSummary() {

        List<Expense> expenses = expenseRepository.findAll();

        BigDecimal total = expenses.stream()
                .map(expense -> BigDecimal.valueOf(expense.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        Map<ExpenseCategory, List<Expense>> grouped =
                new EnumMap<>(ExpenseCategory.class);

        for (Expense expense : expenses) {
            grouped.computeIfAbsent(
                    expense.getCategory(), key -> new ArrayList<>()
            ).add(expense);
        }

        List<Map<String, Object>> byCategory = new ArrayList<>();

        grouped.forEach((category, items) -> {

            BigDecimal categoryTotal = items.stream()
                    .map(expense -> BigDecimal.valueOf(expense.getAmount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            Map<String, Object> categorySummary = new LinkedHashMap<>();

            categorySummary.put("category", category.name());
            categorySummary.put("total", categoryTotal);
            categorySummary.put("count", items.size());

            byCategory.add(categorySummary);
        });

        byCategory.sort((a, b) ->
                ((BigDecimal) b.get("total"))
                        .compareTo((BigDecimal) a.get("total"))
        );

        Map<String, Object> summary = new LinkedHashMap<>();

        summary.put("total", total);
        summary.put("byCategory", byCategory);

        return summary;
    }
}

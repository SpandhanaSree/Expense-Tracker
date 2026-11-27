package com.challenge.backend.repository;

import com.challenge.backend.model.Expense;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ExpenseRepository {

    private final List<Expense> expenses = new ArrayList<>();

    public List<Expense> findAll() {
        return expenses;
    }

    public void save(Expense expense) {
        expenses.add(expense);
    }

    public Optional<Expense> findById(String id) {
        return expenses.stream()
                .filter(expense -> expense.getId().equals(id))
                .findFirst();
    }

    public boolean deleteById(String id) {
        return expenses.removeIf(expense -> expense.getId().equals(id));
    }
}

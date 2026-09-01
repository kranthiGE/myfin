package com.sahikran.expense.application.port.out;

import java.util.stream.Stream;

import com.sahikran.expense.domain.Expense;

public interface ExpenseRepositoryPort {
    Stream<Expense> streamAllExpenses();
}
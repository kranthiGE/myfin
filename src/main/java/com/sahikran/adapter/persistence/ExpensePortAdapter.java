package com.sahikran.adapter.persistence;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.sahikran.expense.application.port.out.ExpenseRepositoryPort;
import com.sahikran.expense.domain.Expense;

public class ExpensePortAdapter implements ExpenseRepositoryPort {

    private final ExpenseRepository expenseRepository;

    public ExpensePortAdapter(ExpenseRepository expenseRepository){
        this.expenseRepository = expenseRepository;
    }

    @Override
    public Stream<Expense> streamAllExpenses() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(expenseRepository, Spliterator.ORDERED), false);
    }
    
}

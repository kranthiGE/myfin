package com.sahikran.expense.domain;

public record ExpenseMetric<T> (
    String id, 
    Class<?> type,
    String description
) {}

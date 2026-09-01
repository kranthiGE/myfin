package com.sahikran.expense.domain;

import java.time.LocalDate;

public record Expense(LocalDate spentDate, String expenseDetails, 
            Double withdrawlAmount, Double depositedAmount, String bankName) {
    
}

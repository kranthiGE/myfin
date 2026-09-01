package com.sahikran.infra.config;

import com.sahikran.adapter.persistence.ExpenseExcelRepository;
import com.sahikran.adapter.persistence.ExpenseRepository;

public class ExpenseRepositoryFactory {
    
    /**
     * to instantiate ExpenseRepository reading the expense file from AppConfig properties
     * This factory class has to be in infra package to isolate the persistent adapters or domain from enviornment specific file path details
     * @return
     */
    public static ExpenseRepository create(){
        String expenseFilePath = ExpenseRepositoryFactory.class.getClassLoader().getResource(AppConfig.getInstance().get("expense.file")).getPath();

        if(expenseFilePath == null || expenseFilePath.isBlank()){
            return null;
        }

        try {
            return new ExpenseRepository.Builder().build(
                new ExpenseExcelRepository.Builder()
                    .addExpenseFilePath(expenseFilePath)
                    .build()
                    .getExpenseInputStream(), 
                3
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate ExpenseRepository ", e);
        }
    }
}

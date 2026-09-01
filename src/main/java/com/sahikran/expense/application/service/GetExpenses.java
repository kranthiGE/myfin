package com.sahikran.expense.application.service;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sahikran.expense.application.port.out.ExpenseRepositoryPort;
import com.sahikran.expense.application.port.out.LoadExpenseCategoryPort;
import com.sahikran.expense.domain.Expense;

public class GetExpenses  {
    
    private static final Logger log = LoggerFactory.getLogger(GetExpenses.class);

    private final LoadExpenseCategoryPort<Map<String, String>> expenseCategoryPort;

    private final ExpenseRepositoryPort expenseRepositoryPort;

    public GetExpenses(LoadExpenseCategoryPort<Map<String, String>> expenseCategoryPort,
        ExpenseRepositoryPort expenseRepositoryPort
    ){
        this.expenseCategoryPort = expenseCategoryPort;
        this.expenseRepositoryPort = expenseRepositoryPort;
    }

    public Map<YearMonth, Map<String, Double >> get() {
        log.info("fetching the expenses from repository...");

        // load the rules from /resources/categories.json
        Map<String, String> categoryRules = expenseCategoryPort.loadCategoriesWithSubStrings();
        log.info("category rules fetched.");
        
        Map<YearMonth, Map<String, Double >> yearWiseExpenses = new TreeMap<>();
        // for each expense entry, find the expense description and
        // apply the category rule to bucket-ise the expenses.
        try(Stream<Expense> expenseStream = expenseRepositoryPort.streamAllExpenses()){
            log.info("stream of expenses fetched, calculating year-wise aggregates...");
            
            expenseStream.forEach(expenseEntry -> {
                //1. find the expense date
                YearMonth yearMonth = YearMonth.from(expenseEntry.spentDate());

                //2. get the category from expense details matching to category rules
                String expenseDetails = expenseEntry.expenseDetails();
                String category = findCategory(categoryRules, expenseDetails);

                //3. update the nested map
                yearWiseExpenses.computeIfAbsent(yearMonth, k -> new HashMap<>())
                                .merge(category, expenseEntry.withdrawlAmount(), Double::sum);
                }
            );
        }
        log.info("year wise expenses calculated.");
        return yearWiseExpenses;
    }

    private String findCategory(Map<String, String> categoryRules, String expenseDetails){
        if (expenseDetails == null || expenseDetails.isEmpty()) return "Unknown";

        String normalizedDesc = expenseDetails.toLowerCase();

        return categoryRules.entrySet().stream()
            .filter(rule -> normalizedDesc.contains(rule.getKey().toLowerCase()))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse("Other");
    }
}

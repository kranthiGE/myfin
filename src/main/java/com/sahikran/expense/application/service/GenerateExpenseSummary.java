package com.sahikran.expense.application.service;

import java.time.Year;
import java.time.YearMonth;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sahikran.adapter.persistence.ExpenseCategoryRepository;
import com.sahikran.adapter.persistence.ExpensePortAdapter;
import com.sahikran.expense.domain.ExpenseMetric;
import com.sahikran.infra.config.ExpenseRepositoryFactory;

public class GenerateExpenseSummary {

    private static final Logger log = LoggerFactory.getLogger(GenerateExpenseSummary.class);

    public static final ExpenseMetric<Double> TOTAL_CURRENTYEAR_SPEND = new ExpenseMetric<>("total_cy_spend", Double.class, "Total annual expenses for the current year");
    public static final ExpenseMetric<LinkedHashMap<YearMonth, Double>> TOTAL_CY_MONTHLY_SPEND = new ExpenseMetric<>("total_cy_monthly_spend", LinkedHashMap.class, "Total monthly expenses for current year");

    public RegisterExpenseMetric get(){
        log.info("fetching the expenses from repository...");

        GetExpenses expenseService = new GetExpenses(
            new ExpenseCategoryRepository(), 
            new ExpensePortAdapter(ExpenseRepositoryFactory.create())
        );
        // fetch and sort the map
        Map<YearMonth, Map<String, Double >> yearWiseExpenses = expenseService.get();

        RegisterExpenseMetric registerExpenseMetric = new RegisterExpenseMetric();
        
        log.info("Registering metrics and its calculations...");
        // metric 1: add calculation for total annual spend
        // use peek for debugging
        // ex: .peek(entry -> log.debug("yearmonth = " + entry.getKey()))
        Double currentYearTotal = yearWiseExpenses.entrySet().stream()
                        .filter(entry -> entry.getKey().getYear() == Year.now().getValue())// filter current year
                        .map(entry -> entry.getValue().values())
                        .flatMap(Collection::stream)
                        .mapToDouble(Double::doubleValue)
                        .sum();

        Supplier<Double> supplier = () -> {
            return currentYearTotal;
        };

        registerExpenseMetric.<Double>register(TOTAL_CURRENTYEAR_SPEND, supplier);
        
        // metric 2: add calculation for total monthly spend
        LinkedHashMap<YearMonth, Double> monthlyExpensesMap = yearWiseExpenses.entrySet().stream()
        .filter(entry -> entry.getKey().getYear() == Year.now().getValue())
        .collect(
            Collectors.groupingBy(Map.Entry::getKey, 
                LinkedHashMap::new,
                Collectors.summingDouble(entry -> entry.getValue().values().stream()
                    .mapToDouble(Double::doubleValue)
                    .sum()
                )
            )
        );
        
        Supplier<LinkedHashMap<YearMonth, Double>> supplier2 = () -> {
            return monthlyExpensesMap;
        };

        registerExpenseMetric.<LinkedHashMap<YearMonth, Double>>register(TOTAL_CY_MONTHLY_SPEND, supplier2);

        log.info("Summary generated.");
        return registerExpenseMetric;
    }
}

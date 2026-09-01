package com.sahikran.expense.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.time.Instant;
import java.time.YearMonth;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sahikran.adapter.persistence.ExpenseCategoryRepository;
import com.sahikran.adapter.persistence.ExpensePortAdapter;
import com.sahikran.expense.application.service.GetExpenses;
import com.sahikran.infra.config.ExpenseRepositoryFactory;

public class GetExpensesTest {
    
    private static final Logger log = LoggerFactory.getLogger(GetExpensesTest.class);

    @Test
    public void whenExpenseExcelIsPassed_returnYearWiseExpenses() 
    throws InterruptedException, ExecutionException{

        GetExpenses getExpenseService = new GetExpenses(
            new ExpenseCategoryRepository(), 
            new ExpensePortAdapter(ExpenseRepositoryFactory.create())
        );
        // fetch and sort the map
        Map<YearMonth, Map<String, Double >> yearWiseExpenses = getExpenseService.get().entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .collect(
                                Collectors.toMap(
                                    Map.Entry::getKey, 
                                    Map.Entry::getValue
                                )
                            );
        assertNotNull(yearWiseExpenses);

        Instant start = Instant.now();

        yearWiseExpenses.forEach((yearMonth, categoryMap) -> {
                    log.debug("Month: " + yearMonth);
                    categoryMap.forEach((category, total) -> {
                            assertNotNull(total);
                            System.out.println(String.format("  %-20s : %.2f",  category, total));
                        });
                });

        yearWiseExpenses.entrySet().stream()
                .collect(
                    Collectors.toMap(
                        Map.Entry::getKey, 
                        entry -> entry.getValue().values().stream()
                                    .mapToDouble(Double::doubleValue)
                                    .sum()
                    )
                ).forEach((yearMonth, total) -> {
                log.debug("yearmonth = " + yearMonth + ", total = "+ total);
            });

        log.debug("elapsed time = " + Duration.between(start, Instant.now()).toMillis());
    }

}

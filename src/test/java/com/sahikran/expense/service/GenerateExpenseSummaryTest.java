package com.sahikran.expense.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.time.YearMonth;
import java.util.LinkedHashMap;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sahikran.expense.application.service.GenerateExpenseSummary;
import com.sahikran.expense.application.service.RegisterExpenseMetric;

public class GenerateExpenseSummaryTest {

    private static final Logger log = LoggerFactory.getLogger(GenerateExpenseSummaryTest.class);

    @Test
    public void whenExpensesArePassed_returnSummaryMetrics(){
        Instant start = Instant.now();
        GenerateExpenseSummary generate = new GenerateExpenseSummary();
        RegisterExpenseMetric expenseMetrics = generate.get();
        log.debug("calculating...");
        assertEquals(2232302.61, expenseMetrics.getValue(GenerateExpenseSummary.TOTAL_CURRENTYEAR_SPEND));

        LinkedHashMap<YearMonth, Double> monthlyExpensesMap = expenseMetrics.getValue(GenerateExpenseSummary.TOTAL_CY_MONTHLY_SPEND);
        assertEquals(6, monthlyExpensesMap.size());
        monthlyExpensesMap.forEach((month, total) -> {
            log.info("Expenses for month " + month.getMonth() + ", " + month.getYear() + " are " + total);
        });
        log.info("Time taken to generate summary in milliseconds is " + Duration.between(start, Instant.now()).toMillis());
    }
}

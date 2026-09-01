package com.sahikran.expense.application.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.sahikran.expense.domain.ExpenseMetric;

public class RegisterExpenseMetric {
    private final Map<ExpenseMetric<?>, Supplier<?>> metrics = new HashMap<>();

    public <T> void register(ExpenseMetric<T> key, Supplier<T> calculation){
        metrics.put(key, calculation);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(ExpenseMetric<T> key){
        Supplier<?> supplier = metrics.get(key);
        // if supplier is null then throw an exception
        if(supplier == null){
            throw new RuntimeException("given metric key not found");
        }
        return (T) key.type().cast(supplier.get());
    }

    public Map<String, MetricSnapShot> getSummarySnapShot(){
        Map<String, MetricSnapShot> snapShot = new HashMap<>();
        metrics.forEach((key, value) -> {
            snapShot.put(key.id(), new MetricSnapShot(key.description(), value.get()));
        });
        return snapShot;
    }

    public record MetricSnapShot(String description, Object value){}
    
}

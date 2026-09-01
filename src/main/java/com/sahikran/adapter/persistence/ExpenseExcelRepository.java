package com.sahikran.adapter.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

public class ExpenseExcelRepository {
    
    private static final Logger log = LoggerFactory.getLogger(ExpenseExcelRepository.class);

    private final InputStream expenseInputStream;
    
    private ExpenseExcelRepository(InputStream expenseInputStream){
        // dont allow to create a direct instance
        this.expenseInputStream = expenseInputStream;
    }

    public InputStream getExpenseInputStream() {
        return expenseInputStream;
    }

    @JsonPOJOBuilder(withPrefix = "add")
    public static final class Builder{
        private InputStream expenseInputFileStream;

        public Builder addExpenseFilePath(String expenseFilePath){
            Objects.requireNonNull(expenseFilePath, "expenseFilePath can not be empty");
            expenseInputFileStream = getExpenseFileInputStream(expenseFilePath);
            return this;
        }

        public ExpenseExcelRepository build(){
            return new ExpenseExcelRepository(expenseInputFileStream);
        }

        private InputStream getExpenseFileInputStream(String filePath){
            InputStream inputStream = null;
            try {
                inputStream = Files.newInputStream(Path.of(filePath));
            } catch (IOException e) {
                log.error("while creating an Input stream from " + filePath, e);
            }
            return inputStream;
        }
    }

}

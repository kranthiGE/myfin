package com.sahikran.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.sahikran.exception.ReaderException;
import com.sahikran.expense.domain.Expense;

public class ExpenseRepositoryTest {
    
    public static File getInputFile(String inputFileName){
        return new File(ExpenseRepositoryTest.class.getClassLoader().getResource(inputFileName).getFile());
    }

    private InputStream getExpenseFileInputStream(String xlsFilePath){
        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(Path.of(xlsFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    private static List<Arguments> provideMutlipleFileInputs(){
        return Arrays.asList(
          Arguments.of(getInputFile("expense-statements.xlsx").getAbsolutePath(), 47)
        );
    }

    @ParameterizedTest
    @DisplayName("when multiple Excel input files are passed, program should parse and return expected number of expense items and their count should match")
    @MethodSource("provideMutlipleFileInputs")
    public void whenSimpleExcelIsPassed_readExpectedSizeOfItems(String xlsFilePath, int expected) 
    throws IOException, ReaderException 
        {
        try(ExpenseRepository xlsItemIterator = new ExpenseRepository.Builder().build(getExpenseFileInputStream(xlsFilePath), 3)){
            int count = 0;
            while(xlsItemIterator.hasNext()){
                Expense expenseEntry = xlsItemIterator.next();
                String expenseDetails = expenseEntry.expenseDetails();
                assertNotNull(expenseDetails);
                System.out.println(expenseEntry.spentDate().toString());
                count++;
            }
            assertEquals(expected, count);
        } 
    }

}


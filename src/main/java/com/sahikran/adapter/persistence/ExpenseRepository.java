package com.sahikran.adapter.persistence;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.CellType;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sahikran.exception.ReaderException;
import com.sahikran.expense.domain.Expense;

public class ExpenseRepository implements Iterator<Expense>, Closeable {

    private static final Logger log = LoggerFactory.getLogger(ExpenseRepository.class);
    private final Sheet sheet;
    private final int headerEndRowIndex;
    private final ReadableWorkbook readableWorkbook;

    private Expense nextItem;
    private boolean isHeaderSkipped;
    private Iterator<Row> rowIterator;
    private Stream<Row> rowStream;

    private ExpenseRepository(Sheet sheet, ReadableWorkbook readableWorkbook, int headerEndRowIndex) 
    throws IOException{
        this.sheet = sheet;
        this.readableWorkbook = readableWorkbook;
        this.headerEndRowIndex = headerEndRowIndex;
        nextItem = null;
        isHeaderSkipped = false;
        rowStream = sheet.openStream();
        rowIterator = rowStream.iterator();
    }

    @Override
    public boolean hasNext() {
        if(nextItem == null){
            nextItem = next();
        }
        return nextItem != null;
    }

    @Override
    public Expense next() {
        if(nextItem != null){
            var next = nextItem;
            nextItem = null;
            return next;
        }
        // if nextItem is null then execute the below
        int rowCount = 0;
        // skip the header rows
        while(rowIterator.hasNext()){
            Row row = rowIterator.next();
            if(!isHeaderSkipped){
                // skip the rows till the given header end index
                if(rowCount < headerEndRowIndex + 1){
                    log.debug("skipping row: " + rowCount);
                    rowCount++;
                    continue;
                }
                isHeaderSkipped = true;
            }
            // read the cell data of the row
            return mapRowToEntry(row);
        }
        return null;
    }

    private Expense mapRowToEntry(Row row){
        if (row == null) return null;
        return new Expense(parseDateByManyFormats(getCellValueAsString(row.getCell(0))), 
                            row.getCellAsString(1).get(),
                            Double.valueOf(getCellValueAsString(row.getCell(3))), 
                            Double.valueOf(getCellValueAsString(row.getCell(4))),
                            row.getCellAsString(6).get());
    }
    
    public static class Builder{
        public Builder(){

        }

        /**
         * 
         * @param inputStream input stream of the excel file
         * @param headerEndRowIndex number of rows to be skipped to avoid reading header 
         * @return
         * @throws ReaderException
         */
        public ExpenseRepository build(InputStream inputStream, int headerEndRowIndex) 
        throws ReaderException{
            try {
                ReadableWorkbook readableWorkbook = new ReadableWorkbook(inputStream);
                return new ExpenseRepository(readableWorkbook.getFirstSheet(), readableWorkbook, headerEndRowIndex);
            } catch (IOException e) {
                throw new ReaderException("exception occurred when creating instance", e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        if(rowStream != null){
            rowStream.close();
        }
        if(readableWorkbook != null){
            readableWorkbook.close();
        }
    }

    private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
        DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()),
        DateTimeFormatter.ofPattern("dd/MM/yy", Locale.getDefault()),
        DateTimeFormatter.ofPattern("dd/mm/yyyy", Locale.getDefault()),
        DateTimeFormatter.ofPattern("dd-MMM-yy", Locale.getDefault()),
        DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.getDefault())
    );

    public static LocalDate parseDateByManyFormats(String dateString){
        for (DateTimeFormatter dateTimeFormatter : FORMATTERS) {
            try {
                return LocalDate.parse(dateString.trim(), dateTimeFormatter);
            } catch (DateTimeParseException e) {
                // suppress exception and dont throw because the input date does not match to any of the expected formats. 
            }   
        }
        if(isNumeric(dateString)){
            // Assuming EPOCH date value,try to convert into date
            long excelSerial = (long) Double.parseDouble(dateString);
            return LocalDate.of(1899, 12, 30).plusDays(excelSerial);
        }
        throw new DateTimeParseException("did not match with any formatter " + dateString, dateString, 0);
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null || cell.getType() == CellType.EMPTY) {
            return "0.0";
        }

        switch (cell.getType()) {
            case NUMBER:
                // fastexcel identifies dates by checking their internal format string.
                // If the format string contains date patterns like 'y', 'm', or 'd',
                // it's likely a date.
                String format = cell.getDataFormatString();
                if (format != null && (format.contains("y") || format.contains("m") || format.contains("d"))) {
                    try {
                        // asDate() safely converts the Excel serial number to LocalDateTime
                        return cell.asDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
                    } catch (Exception e) {
                        log.warn("Failed to parse date cell at column {}", cell.getColumnIndex());
                    }
                }
                // If it's not a date, return the plain number
                return cell.asNumber().toPlainString();

            case STRING:
                String strVal = cell.asString();
                return (strVal == null || strVal.isBlank()) ? "0.0" : strVal;

            case FORMULA:
                return cell.getText() != null ? cell.getText() : "0.0";

            default:
                return "0.0";
        }
    }

    
    
}

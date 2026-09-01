package com.sahikran.adapter.persistence;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sahikran.expense.application.port.out.LoadExpenseCategoryPort;
import com.sahikran.infra.config.AppConfig;

public class ExpenseCategoryRepository implements LoadExpenseCategoryPort<Map<String, String>>  {

    @Override
    public Map<String, String> loadCategoriesWithSubStrings() {
        try {
            return loadRulesFromJson(new File(ExpenseCategoryRepository.class.getClassLoader().getResource(AppConfig.getInstance().get("file.categories")).getFile())
                                                        .getAbsolutePath());
        } catch (IOException e) {
            throw new UncheckedIOException("while loading category rules", e);
        }
    }

    private Map<String, String> loadRulesFromJson(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        // Maps the JSON file directly to a Map<String, String>
        return mapper.readValue(new File(filePath), new TypeReference<Map<String, String>>() {});
    }
    
}

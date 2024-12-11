package org.loudsheep.psio_project.backend.trading.validators;

import org.loudsheep.psio_project.backend.trading.TradingFormula;
import org.loudsheep.psio_project.backend.trading.TradingFormulaValidator;
import org.loudsheep.psio_project.backend.trading.formulas.RandomTradingFormula;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RandomTradingFormulaValidator implements TradingFormulaValidator {
    public RandomTradingFormulaValidator() {
    }

    @Override
    public String[] validate(Map<String, Object> formData) {
        List<String> errors = new ArrayList<>();

        if (!formData.containsKey("budget") || !(formData.get("budget") instanceof Double)) {
            errors.add("Budget is required and must be a number value.");
        } else {
            if ((double) formData.get("budget") <= 0) {
                errors.add("Budget must be a positive number");
            }
        }

        return errors.toArray(new String[0]);
    }

    @Override
    public TradingFormula create(Map<String, Object> formData) {
        return new RandomTradingFormula((Double) formData.get("budget"));
    }
}

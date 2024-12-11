package org.loudsheep.psio_project.backend.trading.validators;

import org.loudsheep.psio_project.backend.trading.TradingFormula;
import org.loudsheep.psio_project.backend.trading.TradingFormulaValidator;
import org.loudsheep.psio_project.backend.trading.formulas.SimpleUpAndDownTradingFormula;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleUpAndDownTradingFormulaValidator implements TradingFormulaValidator {
    public SimpleUpAndDownTradingFormulaValidator() {
    }

    @Override
    public String[] validate(Map<String, Object> formData) {
        List<String> errors = new ArrayList<>();

        if (!formData.containsKey("budget") || !(formData.get("budget") instanceof Number)) {
            errors.add("Budget is required and must be a number value.");
        } else {
            if ((double) formData.get("budget") <= 0) {
                errors.add("Budget must be a positive number");
            }
        }

        if (!formData.containsKey("daysBackToCheck") || !(formData.get("daysBackToCheck") instanceof Number)) {
            errors.add("daysBackToCheck is required and must be an integer.");
        } else {
            int daysBackToCheck = ((Number) formData.get("daysBackToCheck")).intValue();
            if (daysBackToCheck <= 0)
                errors.add("daysBackToCheck must be non-negative.");
        }

        return errors.toArray(new String[0]);
    }

    @Override
    public TradingFormula create(Map<String, Object> formData) {
        return new SimpleUpAndDownTradingFormula((Double) formData.get("budget"), ((Number) formData.get("daysBackToCheck")).intValue());
    }
}

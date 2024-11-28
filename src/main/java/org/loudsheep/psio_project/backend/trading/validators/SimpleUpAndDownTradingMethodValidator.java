package org.loudsheep.psio_project.backend.trading.validators;

import org.loudsheep.psio_project.backend.trading.TradingMethod;
import org.loudsheep.psio_project.backend.trading.TradingMethodValidator;
import org.loudsheep.psio_project.backend.trading.methods.SimpleUpAndDownTradingMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleUpAndDownTradingMethodValidator implements TradingMethodValidator {
    public SimpleUpAndDownTradingMethodValidator() {
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

        if (!formData.containsKey("daysBackToCheck") || !(formData.get("daysBackToCheck") instanceof Integer)) {
            errors.add("daysBackToCheck is required and must be an integer.");
        } else {
            int age = (int) formData.get("daysBackToCheck");
            if (age <= 0) {
                errors.add("daysBackToCheck must be non-negative.");
            }
        }

        return errors.toArray(new String[0]);
    }

    @Override
    public TradingMethod create(Map<String, Object> formData) {
        return new SimpleUpAndDownTradingMethod((Double) formData.get("budget"), (Integer) formData.get("daysBackToCheck"));
    }
}

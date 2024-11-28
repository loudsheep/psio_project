package org.loudsheep.psio_project.backend.trading.validators;

import org.loudsheep.psio_project.backend.trading.TradingMethod;
import org.loudsheep.psio_project.backend.trading.TradingMethodValidator;
import org.loudsheep.psio_project.backend.trading.methods.RandomTradingMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RandomTradingMethodValidator implements TradingMethodValidator {
    public RandomTradingMethodValidator() {
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
    public TradingMethod create(Map<String, Object> formData) {
        return new RandomTradingMethod((Double) formData.get("budget"));
    }
}

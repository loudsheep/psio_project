package org.loudsheep.psio_project.backend.trading.validators;

import org.loudsheep.psio_project.backend.trading.TradingMethod;
import org.loudsheep.psio_project.backend.trading.TradingMethodValidator;
import org.loudsheep.psio_project.backend.trading.methods.SimpleThresholdTradingMethod;
import org.loudsheep.psio_project.backend.trading.methods.SimpleUpAndDownTradingMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleThresholdTradingMethodValidator implements TradingMethodValidator {
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

        if (!formData.containsKey("buyThreshold") || !(formData.get("buyThreshold") instanceof Number)) {
            errors.add("buyThreshold is required and must be a number value.");
        } else {
            double buyThreshold = (double) formData.get("buyThreshold");
            if (buyThreshold <= 0)
                errors.add("buyThreshold must be non-negative.");
        }

        if (!formData.containsKey("sellThreshold") || !(formData.get("sellThreshold") instanceof Number)) {
            errors.add("sellThreshold is required and must be a number value.");
        } else {
            double sellThreshold = (double) formData.get("sellThreshold");
            if (sellThreshold <= 0)
                errors.add("sellThreshold must be non-negative.");
        }

        return errors.toArray(new String[0]);
    }

    @Override
    public TradingMethod create(Map<String, Object> formData) {
        return new SimpleThresholdTradingMethod((Double) formData.get("budget"), (double) formData.get("buyThreshold") , (double) formData.get("sellThreshold"));
    }
}

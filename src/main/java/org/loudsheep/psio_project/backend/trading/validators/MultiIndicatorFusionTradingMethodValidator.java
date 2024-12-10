package org.loudsheep.psio_project.backend.trading.validators;

import org.loudsheep.psio_project.backend.trading.TradingMethod;
import org.loudsheep.psio_project.backend.trading.TradingMethodValidator;
import org.loudsheep.psio_project.backend.trading.methods.MultiIndicatorFusionTradingMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultiIndicatorFusionTradingMethodValidator implements TradingMethodValidator {
    public MultiIndicatorFusionTradingMethodValidator() {
    }

    @Override
    public String[] validate(Map<String, Object> formData) {
        List<String> errors = new ArrayList<>();

        if (!formData.containsKey("budget") || !(formData.get("budget") instanceof Number)) {
            errors.add("Budget is required and must be a number value.");
        } else {
            if ((double) formData.get("budget") <= 0) {
                errors.add("Budget must be a positive number.");
            }
        }

        if (!formData.containsKey("rsiPeriod") || !(formData.get("rsiPeriod") instanceof Number)) {
            errors.add("RSI period is required and must be an integer.");
        } else {
            int rsiPeriod = ((Number) formData.get("rsiPeriod")).intValue();
            if (rsiPeriod <= 0) {
                errors.add("RSI period must be greater than 0.");
            }
        }

        if (!formData.containsKey("shortEmaPeriod") || !(formData.get("shortEmaPeriod") instanceof Number)) {
            errors.add("Short EMA period is required and must be an integer.");
        } else {
            int shortEmaPeriod = ((Number) formData.get("shortEmaPeriod")).intValue();
            if (shortEmaPeriod <= 0) {
                errors.add("Short EMA period must be greater than 0.");
            }
        }

        if (!formData.containsKey("longEmaPeriod") || !(formData.get("longEmaPeriod") instanceof Number)) {
            errors.add("Long EMA period is required and must be an integer.");
        } else {
            int longEmaPeriod = ((Number) formData.get("longEmaPeriod")).intValue();
            if (longEmaPeriod <= 0) {
                errors.add("Long EMA period must be greater than 0.");
            }
        }

        if (formData.containsKey("shortEmaPeriod") && formData.containsKey("longEmaPeriod")) {
            int shortEmaPeriod = ((Number) formData.get("shortEmaPeriod")).intValue();
            int longEmaPeriod = ((Number) formData.get("longEmaPeriod")).intValue();
            if (shortEmaPeriod >= longEmaPeriod) {
                errors.add("Short EMA period must be less than Long EMA period.");
            }
        }

        if (!formData.containsKey("bollingerPeriod") || !(formData.get("bollingerPeriod") instanceof Number)) {
            errors.add("Bollinger period is required and must be an integer.");
        } else {
            int bollingerPeriod = ((Number) formData.get("bollingerPeriod")).intValue();
            if (bollingerPeriod <= 0) {
                errors.add("Bollinger period must be greater than 0.");
            }
        }

        if (!formData.containsKey("bollingerMultiplier") || !(formData.get("bollingerMultiplier") instanceof Number)) {
            errors.add("Bollinger multiplier is required and must be a number.");
        } else {
            double bollingerMultiplier = (double) formData.get("bollingerMultiplier");
            if (bollingerMultiplier <= 0) {
                errors.add("Bollinger multiplier must be greater than 0.");
            }
        }

        return errors.toArray(new String[0]);
    }

    @Override
    public TradingMethod create(Map<String, Object> formData) {
        return new MultiIndicatorFusionTradingMethod(
                (Double) formData.get("budget"),
                ((Number) formData.get("rsiPeriod")).intValue(),
                ((Number) formData.get("shortEmaPeriod")).intValue(),
                ((Number) formData.get("longEmaPeriod")).intValue(),
                ((Number) formData.get("bollingerPeriod")).intValue(),
                (Double) formData.get("bollingerMultiplier")
        );
    }
}

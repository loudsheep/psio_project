package org.loudsheep.psio_project.backend.trading;

import java.util.Map;

public interface TradingMethodValidator {
    String[] validate(Map<String, Object> formData);
    TradingMethod create(Map<String, Object> formData);
}

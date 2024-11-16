package org.loudsheep.psio_project.backend.strategies;

import java.util.Map;

public interface Validatable {
    String[] validateData(Map<String, Object> formData);
    Validatable create(Map<String, Object> formData);
}

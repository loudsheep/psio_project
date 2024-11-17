package org.loudsheep.psio_project.backend.strategies;

import java.util.Map;

public interface Validatable {
    static String[] validateData(Map<String, Object> formData) {
        return new String[]{"Error validating - method not implemented"};
    }

    static Validatable create(Map<String, Object> formData) {
        return null;
    }
}

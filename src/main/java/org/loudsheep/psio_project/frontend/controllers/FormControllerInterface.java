package org.loudsheep.psio_project.frontend.controllers;

import java.util.Map;

public interface FormControllerInterface {
    void setParams(Map<String, Object> params);

    void setSubmitCallback(FormSubmitCallback callback);
}

@FunctionalInterface
interface FormSubmitCallback {
    void onSubmit(Map<String, Object> formData);
}

@FunctionalInterface
interface FormErrorCallback {
    void setError(String text);
}
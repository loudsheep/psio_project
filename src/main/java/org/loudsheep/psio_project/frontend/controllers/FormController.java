package org.loudsheep.psio_project.frontend.controllers;

import java.util.Map;

public interface FormController {
    void setParams(Map<String, Object> params);
    void setSubmitCallback(FormSubmitCallback callback);
    void setError(String text);
}

@FunctionalInterface
interface FormSubmitCallback {
    void onSubmit(Map<String, Object> formData);
}
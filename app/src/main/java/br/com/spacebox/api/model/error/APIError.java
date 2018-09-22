package br.com.spacebox.api.model.error;

import java.util.ArrayList;
import java.util.List;

public class APIError {
    private List<String> errors;

    public String getError() {
        return errors != null && !errors.isEmpty() ? errors.get(0) : null;
    }

    public void addError(String error) {
        errors = new ArrayList<>();
        errors.add(error);
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}

package br.com.spacebox.api.model.error;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class APIError {
    private int code;
    private List<String> errors;

    public String getError() {
        return errors != null && !errors.isEmpty() ? errors.get(0) : null;
    }

    public void addError(String error) {
        errors = new ArrayList<>();
        errors.add(error);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}

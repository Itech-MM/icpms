package org.flexitech.projects.icpms.common;

import java.util.ArrayList;
import java.util.List;

public class FileValidationResult {
    private boolean valid;
    private List<String> errors = new ArrayList<>();

    // Getters and setters
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}

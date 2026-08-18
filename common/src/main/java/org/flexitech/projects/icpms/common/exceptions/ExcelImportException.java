package org.flexitech.projects.icpms.common.exceptions;

import java.util.List;

import org.flexitech.projects.icpms.common.excel.ImportError;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcelImportException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1888144296620716449L;
	private String code;
    private List<ImportError> errors;

    public ExcelImportException(String message) {
        super(message);
    }

    public ExcelImportException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ExcelImportException(String message, List<ImportError> errors) {
        super(message);
        this.errors = errors;
    }
}

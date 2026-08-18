package org.flexitech.projects.icpms.common.excel;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportError {
	@ExcelProperty("Row Number")
    private Integer rowNumber;
    
    @ExcelProperty("Field")
    private String field;
    
    @ExcelProperty("Value")
    private String value;
    
    @ExcelProperty("Error Message")
    private String errorMessage;
}

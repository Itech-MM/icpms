package org.flexitech.projects.icpms.common.image;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ZipExtractionResult {
    private File excelFile;
    private Map<String, File> imageFiles = new HashMap<>();
    private String extractionPath;
}
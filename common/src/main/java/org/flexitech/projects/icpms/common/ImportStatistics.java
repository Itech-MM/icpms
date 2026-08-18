package org.flexitech.projects.icpms.common;

public class ImportStatistics {
    private int totalImports;
    private int failedImports;
    private double successRate;
    private String lastImportDate;

    public ImportStatistics(int totalImports, int failedImports, double successRate, String lastImportDate) {
        this.totalImports = totalImports;
        this.failedImports = failedImports;
        this.successRate = successRate;
        this.lastImportDate = lastImportDate;
    }

    // Getters and setters
    public int getTotalImports() { return totalImports; }
    public void setTotalImports(int totalImports) { this.totalImports = totalImports; }
    public int getFailedImports() { return failedImports; }
    public void setFailedImports(int failedImports) { this.failedImports = failedImports; }
    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    public String getLastImportDate() { return lastImportDate; }
    public void setLastImportDate(String lastImportDate) { this.lastImportDate = lastImportDate; }
}

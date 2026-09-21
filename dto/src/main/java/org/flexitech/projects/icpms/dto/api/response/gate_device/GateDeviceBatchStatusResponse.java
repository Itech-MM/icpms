package org.flexitech.projects.icpms.dto.api.response.gate_device;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GateDeviceBatchStatusResponse {

    private int requested;
    private int updated;
    private List<FailedItem> failed;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FailedItem {
        private Long deviceId;
        private String reason;
    }
}
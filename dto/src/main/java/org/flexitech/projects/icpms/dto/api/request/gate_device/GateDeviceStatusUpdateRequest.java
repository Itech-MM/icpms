package org.flexitech.projects.icpms.dto.api.request.gate_device;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GateDeviceStatusUpdateRequest {

    @NotNull(message = "deviceId is required")
    private Long deviceId;

    @NotNull(message = "healthStatus is required")
    private Integer healthStatus;

    private Integer latencyMs;

    private String statusNote;
}
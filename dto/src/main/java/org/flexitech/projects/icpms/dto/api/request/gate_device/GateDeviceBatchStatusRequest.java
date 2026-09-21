package org.flexitech.projects.icpms.dto.api.request.gate_device;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GateDeviceBatchStatusRequest {

    @NotEmpty(message = "devices list cannot be empty")
    @Valid
    private List<GateDeviceStatusUpdateRequest> devices;
}
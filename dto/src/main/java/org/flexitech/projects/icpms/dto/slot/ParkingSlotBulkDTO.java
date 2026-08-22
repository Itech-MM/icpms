package org.flexitech.projects.icpms.dto.slot;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParkingSlotBulkDTO {

    private Long siteId;
    private String prefix;
    private String floorLevel;
    private Integer status;
    private Boolean isVip = false;

    @NotNull(message = "'From' is required")
    @Min(value = 1, message = "'From' must be at least 1")
    private Integer fromNumber;

    @NotNull(message = "'To' is required")
    @Min(value = 1, message = "'To' must be at least 1")
    private Integer toNumber;

}
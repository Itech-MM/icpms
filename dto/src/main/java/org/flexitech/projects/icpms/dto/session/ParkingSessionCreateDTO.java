package org.flexitech.projects.icpms.dto.session;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingSessionCreateDTO {
    private Long vehicleId;
    private Long entryGateId;
    private Long parkingSlotId;
    private Long tariffId;
    private Long operatorId;
    private Long entryShiftId;
    private String entryPhotoUrl;
}
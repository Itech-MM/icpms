package org.flexitech.projects.icpms.dto.session;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingSessionCloseDTO {
    private Long sessionId;
    private Long exitGateId;
    private BigDecimal totalAmount;
    private String exitPhotoUrl;
    private Long exitShiftId;
    private Boolean isMember;
    private Boolean isFoc;
    private Long tariffId;
    private Long durationMinutes;
    private String remark;
}
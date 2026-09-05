package org.flexitech.projects.icpms.service.common.events;

import org.flexitech.projects.icpms.persistence.entities.audit_logs.VehicleAlertLog;
import org.springframework.context.ApplicationEvent;

public class VehicleAlertCreatedEvent extends ApplicationEvent {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3664442483675187315L;
	
	
	private final VehicleAlertLog alertLog;

    public VehicleAlertCreatedEvent(VehicleAlertLog alertLog) {
        super(alertLog);
        this.alertLog = alertLog;
    }

    public VehicleAlertLog getAlertLog() {
        return alertLog;
    }
}
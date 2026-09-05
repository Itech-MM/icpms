package org.flexitech.projects.icpms.api.listeners;

import org.flexitech.projects.icpms.dto.audit_logs.VehicleAlertLogDTO;
import org.flexitech.projects.icpms.persistence.entities.audit_logs.VehicleAlertLog;
import org.flexitech.projects.icpms.service.common.events.VehicleAlertCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VehicleAlertWebSocketListener {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void onVehicleAlert(VehicleAlertCreatedEvent event) {
        VehicleAlertLog log = event.getAlertLog();
        messagingTemplate.convertAndSend("/topic/vehicle-alerts", new VehicleAlertLogDTO(log));
    }
}
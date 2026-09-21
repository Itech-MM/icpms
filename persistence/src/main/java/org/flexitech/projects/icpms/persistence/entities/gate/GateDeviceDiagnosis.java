package org.flexitech.projects.icpms.persistence.entities.gate;

import java.time.LocalDateTime;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = TableNames.GATE_DEVICE_DIAGNOSIS)
@Entity
@Getter
@Setter
@NoArgsConstructor
public class GateDeviceDiagnosis extends BasedEntity{

	@ManyToOne
	@JoinColumn(name = "gate_device_id")
	private GateDevice gateDevice;
	
	@Column(name = "health_status")
	private Integer healthStatus = 0;

	@Column(name = "latency_ms")
	private Integer latencyMs;

	@Column(name = "checked_at")
	private LocalDateTime checkedAt;

	@Column(name = "status_note", length = 255)
	private String statusNote;
	
	@Column(name = "device_response", columnDefinition = "TEXT")
	private String deviceResponse;
	
}

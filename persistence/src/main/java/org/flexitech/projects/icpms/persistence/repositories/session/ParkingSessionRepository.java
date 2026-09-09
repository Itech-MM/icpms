package org.flexitech.projects.icpms.persistence.repositories.session;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.flexitech.projects.icpms.persistence.entities.session.ParkingSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long>, JpaSpecificationExecutor<ParkingSession> {
	List<ParkingSession> findByVehicleIdAndStatus(Long vehicleId, Integer status);
	Optional<ParkingSession> findFirstByVehicleIdAndStatusOrderByEntryTimeDesc(Long vehicleId, Integer status);
	long countByStatus(Integer status);
	
	@Query("select coalesce(sum(ps.totalAmount), 0) from ParkingSession ps "
			+ "where ps.exitShift.id = :shiftId and ps.status = :completedStatus")
	BigDecimal sumTotalAmountByShiftAndStatus(@Param("shiftId") Long shiftId,
			@Param("completedStatus") Integer completedStatus);
	
	long countByEntryShiftIdAndStatus(Long entryShiftId, Integer status);
	long countByExitShiftIdAndStatus(Long exitShiftId, Integer status);
	
	@Query("SELECT ps FROM ParkingSession ps WHERE ps.status IN :statuses " +
	        "AND (ps.entryGate.id = :gateId OR ps.exitGate.id = :gateId) " +
	        "ORDER BY COALESCE(ps.exitTime, ps.entryTime) DESC")
	Page<ParkingSession> findRecentVisitorsByGate(@Param("statuses") List<Integer> statuses,
	        @Param("gateId") Long gateId, Pageable pageable);
	
}

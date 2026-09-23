package org.flexitech.projects.icpms.persistence.repositories.menu;

import java.util.List;

import org.flexitech.projects.icpms.persistence.entities.menu.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MenuRepository extends JpaRepository<Menu, Long> {
	@Query("SELECT m FROM Menu m WHERE (:status IS NULL OR m.status = :status)")
	List<Menu> findByStatus(@Param("status") Integer status);

	List<Menu> findByIdInAndStatusAndDisplayStatusOrderBySequenceAsc(List<Long> accessibleMenuIds, Integer i, Integer displayStatus);

	List<Menu> findByIdNotInAndStatus(List<Long> accessedMenuIds, Integer i);

	List<Menu> findByParentMenu_Id(Long parentMenuId);

	@Query("SELECT m FROM Menu m WHERE m.parentMenu IS NOT NULL AND m.status = 1 ORDER BY m.sequence ASC")
	List<Menu> findAllActiveParentMenu();

	List<Menu> findByParentMenuIsNullAndStatusOrderBySequenceAsc(Integer status);

	List<Menu> findByParentMenu_IdAndStatusOrderBySequenceAsc(Long parentId, Integer status);

	List<Menu> findByStatusOrderBySequenceAsc(Integer status);

}
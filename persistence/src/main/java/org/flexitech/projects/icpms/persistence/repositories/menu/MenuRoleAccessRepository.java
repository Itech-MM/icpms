package org.flexitech.projects.icpms.persistence.repositories.menu;

import java.util.List;
import java.util.Optional;

import org.flexitech.projects.icpms.persistence.entities.menu.MenuRoleAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MenuRoleAccessRepository extends JpaRepository<MenuRoleAccess, Long> {
	Optional<MenuRoleAccess> findByRoleIdAndMenuId(Long roleId, Long menuId);

	List<MenuRoleAccess> findByRoleId(Long roleId);

	List<MenuRoleAccess> findByMenuId(Long menuId);

	List<MenuRoleAccess> findByRoleIdIn(List<Long> roleIds);

	boolean existsByRoleIdAndMenuId(Long roleId, Long menuId);

	@Query("SELECT mra FROM MenuRoleAccess mra " + "JOIN FETCH mra.menu m " + "WHERE mra.role.id = :roleId "
			+ "AND m.status = 1 " + "ORDER BY m.sequence")
	List<MenuRoleAccess> findByRoleIdWithMenu(@Param("roleId") Long roleId);

	@Query("SELECT DISTINCT mra.menu.id FROM MenuRoleAccess mra " + "WHERE mra.role.id IN :roleIds "
			+ "AND mra.canView = true " + "AND mra.menu.status = 1")
	List<Long> findAccessibleMenuIdsByRoleIds(@Param("roleIds") List<Long> roleIds);

	@Query("SELECT mra FROM MenuRoleAccess mra " + "WHERE mra.role.id = :roleId " + "AND mra.menu.id = :menuId "
			+ "AND (mra.canView = true OR mra.canAccess = true)")
	Optional<MenuRoleAccess> findAccessPermission(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

	void deleteByRoleIdAndMenuIdNotIn(Long roleId, List<Long> menuIds);

	void deleteByMenuIdAndRoleIdNotIn(Long menuId, List<Long> roleIds);

	@Query("SELECT COUNT(mra) > 0 FROM MenuRoleAccess mra " + "JOIN mra.menu m " + "WHERE m.url = :menuUrl "
			+ "AND mra.role.id IN :roleIds " + "AND mra.canAccess = true " + "AND m.status = 1")
	boolean existsByMenuUrlAndRoleIds(@Param("menuUrl") String menuUrl, @Param("roleIds") List<Long> roleIds);

	void deleteByRoleId(Long roleId);

	@Query("SELECT mra.menu.id FROM MenuRoleAccess mra WHERE mra.role.id = :roleId")
	List<Long> findMenuIdsByRoleId(@Param("roleId") Long roleId);
}

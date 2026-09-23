package org.flexitech.projects.icpms.persistence.entities.menu;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;
import org.flexitech.projects.icpms.persistence.entities.role.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = TableNames.MENU_ROLE_ACCESS_TBL, uniqueConstraints = {
    @UniqueConstraint(name = "uk_role_menu", columnNames = {"role_id", "menu_id"})
})
public class MenuRoleAccess extends BasedEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_menus_roles_access_role"))
    private Role role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false, foreignKey = @ForeignKey(name = "fk_menus_roles_access_menu"))
    private Menu menu;
    
    @Column(name = "can_view", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean canView = true;
    
    @Column(name = "can_access", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean canAccess = true;
    
    @Column(name = "can_edit", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean canEdit = false;
    
    @Column(name = "can_delete", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean canDelete = false;
    
    @Column(name = "is_default", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDefault = false;
    
    @Column(name = "permission_priority", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer permissionPriority = 0;
    
    // Constructor
    public MenuRoleAccess() {}
    
    public MenuRoleAccess(Role role, Menu menu) {
        this.role = role;
        this.menu = menu;
    }
    
    public MenuRoleAccess(Role role, Menu menu, Boolean canView, Boolean canAccess, 
                         Boolean canEdit, Boolean canDelete) {
        this.role = role;
        this.menu = menu;
        this.canView = canView;
        this.canAccess = canAccess;
        this.canEdit = canEdit;
        this.canDelete = canDelete;
    }
    
    public boolean hasAnyPermission() {
        return Boolean.TRUE.equals(canView) || 
               Boolean.TRUE.equals(canAccess) || 
               Boolean.TRUE.equals(canEdit) || 
               Boolean.TRUE.equals(canDelete);
    }
    
    public boolean hasFullAccess() {
        return Boolean.TRUE.equals(canView) && 
               Boolean.TRUE.equals(canAccess) && 
               Boolean.TRUE.equals(canEdit) && 
               Boolean.TRUE.equals(canDelete);
    }
    
}

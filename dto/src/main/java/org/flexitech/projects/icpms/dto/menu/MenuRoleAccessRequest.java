package org.flexitech.projects.icpms.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuRoleAccessRequest {
    private Long id;
    private Long roleId;
    private Long menuId;
    private Boolean canView;
    private Boolean canAccess;
    private Boolean canEdit;
    private Boolean canDelete;
    private Boolean isDefault;
    private Integer permissionPriority;
}
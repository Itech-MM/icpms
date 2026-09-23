package org.flexitech.projects.icpms.dto.menu;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuRoleAccessTreeNode {
    private Long id;
    private String name;
    private String icon;
    private String url;
    private Integer level;
    private Boolean hasChildren;
    private Boolean expanded = false;
    
    private Long menuRoleAccessId;
    private Boolean canView;
    private Boolean canAccess;
    private Boolean canEdit;
    private Boolean canDelete;
    private Boolean isDefault;
    private Integer permissionPriority;
    
    private List<MenuRoleAccessTreeNode> children = new ArrayList<>();
}
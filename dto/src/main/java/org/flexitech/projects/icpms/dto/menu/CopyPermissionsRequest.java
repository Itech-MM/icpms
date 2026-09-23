package org.flexitech.projects.icpms.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CopyPermissionsRequest {
    private Long sourceRoleId;
    private Long targetRoleId;
}
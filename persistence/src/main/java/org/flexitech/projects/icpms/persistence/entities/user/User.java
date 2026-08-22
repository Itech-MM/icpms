package org.flexitech.projects.icpms.persistence.entities.user;

import org.flexitech.projects.icpms.common.TableNames;
import org.flexitech.projects.icpms.persistence.BasedEntity;
import org.flexitech.projects.icpms.persistence.entities.role.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = TableNames.USER_TBL)
@Getter
@Setter
public class User extends BasedEntity {
    private String name;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String password;
    private Integer status;
    
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}

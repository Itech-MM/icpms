package org.flexitech.projects.icpms.persistence;

import jakarta.persistence.*;
import lombok.Data;

import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@MappedSuperclass
@Data
public class BasedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_time")
    private Date createdTime;

    @UpdateTimestamp
    @Column(name = "updated_time")
    private Date updatedTime;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
    

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;
    
    @ManyToOne
    @JoinColumn(name = "upload_by")
    private User uploadBy;
}

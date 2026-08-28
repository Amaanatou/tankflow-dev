package com.seneau.tankchlore_service.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "boolean default true")
    private boolean active=true;

    public AbstractEntity(Long id) {
        this.id  = id;
    }

    @CreatedDate
    @Column(name = "createdAt")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    protected LocalDate createdAt;

    @LastModifiedDate
    @Column(name = "updateAt")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    protected LocalDate updatedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
/*
    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "createdBy", referencedColumnName = "id")
    protected Agent createdBy;

    @LastModifiedDate
    @Column(name = "updateOn")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    protected Date updatedOn;

    @LastModifiedBy
    @ManyToOne
    @JoinColumn(name = "updatedBy", referencedColumnName = "id")
    protected Agent updatedBy;

 */
}

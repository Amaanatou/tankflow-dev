package com.seneau.tankchlore_service.data.model;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractType  extends AbstractEntity {

    public AbstractType(Long id) {
        super(id);

    }
    public AbstractType() {

    }
}

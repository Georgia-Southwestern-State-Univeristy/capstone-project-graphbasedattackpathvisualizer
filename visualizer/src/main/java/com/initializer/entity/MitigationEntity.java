package com.initializer.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "mitigation")
public class MitigationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer mitID;

    private String mitName;
    private String mitDesc;

    @OneToMany(mappedBy = "mitigation")
    private List<MitigationEffectEntity> effects;

    public MitigationEntity() {}

    public MitigationEntity(String mitName, String mitDesc) {
        this.mitName = mitName;
        this.mitDesc = mitDesc;
    }

    public Integer getMitID() { return mitID; }
    public String getMitName() { return mitName; }
    public String getMitDesc() { return mitDesc; }
}
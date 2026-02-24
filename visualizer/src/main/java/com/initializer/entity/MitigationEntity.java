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
    private Boolean mitEnabled;

    @OneToMany(mappedBy = "mitigation")
    private List<MitigationEffectEntity> effects;

    public MitigationEntity() {}

    public MitigationEntity(String mitName, String mitDesc, Boolean mitEnabled) {
        this.mitName = mitName;
        this.mitDesc = mitDesc;
        this.mitEnabled = mitEnabled;
    }

    public Integer getMitID() { return mitID; }
    public String getMitName() { return mitName; }
    public String getMitDesc() { return mitDesc; }
    public Boolean getMitEnabled() { return mitEnabled; }
}

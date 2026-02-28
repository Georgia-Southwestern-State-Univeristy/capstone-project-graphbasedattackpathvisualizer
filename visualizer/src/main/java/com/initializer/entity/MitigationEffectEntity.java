package com.initializer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "mitigationeffect")
public class MitigationEffectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer effectID;

    @ManyToOne
    @JoinColumn(name = "mitID")
    private MitigationEntity mitigation;

    @ManyToOne
    @JoinColumn(name = "edgeID")
    private EdgeEntity edge;

    private Integer weightModifier;

    public MitigationEffectEntity() {}

    public MitigationEffectEntity(MitigationEntity mitigation,
                                  EdgeEntity edge,
                                  Integer weightModifier) {
        this.mitigation = mitigation;
        this.edge = edge;
        this.weightModifier = weightModifier;
    }

    public Integer getEffectID() { return effectID; }
    public MitigationEntity getMitigation() { return mitigation; }
    public Integer getWeightModifier() { return weightModifier; }
}

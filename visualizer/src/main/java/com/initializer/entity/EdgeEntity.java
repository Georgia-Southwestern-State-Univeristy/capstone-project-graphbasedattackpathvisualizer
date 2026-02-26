package com.initializer.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "edge")
public class EdgeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer edgeID;

    @ManyToOne
    @JoinColumn(name = "sourceNodeID")
    private NodeEntity sourceNode;

    @ManyToOne
    @JoinColumn(name = "targetNodeID")
    private NodeEntity targetNode;

    private String attackAction;

    private Integer baseWeight;

    @OneToMany(mappedBy = "edge")
    private List<MitigationEffectEntity> mitigationEffects;

    public EdgeEntity() {}

    public EdgeEntity(NodeEntity sourceNode, NodeEntity targetNode,
                      String attackAction, Integer baseWeight) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.attackAction = attackAction;
        this.baseWeight = baseWeight;
    }

    public Integer getEdgeID() { return edgeID; }
    public NodeEntity getSourceNode() { return sourceNode; }
    public NodeEntity getTargetNode() { return targetNode; }
    public String getAttackAction() { return attackAction; }
    public Integer getBaseWeight() { return baseWeight; }

    public List<MitigationEffectEntity> getMitigationEffects() {
    return mitigationEffects;
}
}

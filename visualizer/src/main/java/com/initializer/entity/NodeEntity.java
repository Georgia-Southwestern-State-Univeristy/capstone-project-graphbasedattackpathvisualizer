package com.initializer.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "node")
public class NodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer nodeID;

    private String nodeType;

    private String displayName;

    @OneToMany(mappedBy = "sourceNode")
    private List<EdgeEntity> outgoingEdges;

    @OneToMany(mappedBy = "targetNode")
    private List<EdgeEntity> incomingEdges;

    public NodeEntity() {}

    public NodeEntity(String nodeType, String displayName) {
        this.nodeType = nodeType;
        this.displayName = displayName;
    }

    public Integer getNodeID() { return nodeID; }
    public String getNodeType() { return nodeType; }
    public String getDisplayName() { return displayName; }

    public void setNodeType(String nodeType) { this.nodeType = nodeType; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}

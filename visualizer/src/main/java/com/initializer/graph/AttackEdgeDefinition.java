package com.initializer.graph;


// This class represents a single allowed attack path between two node types in the attack graph
public class AttackEdgeDefinition {
   
    private NodeType from;                      // Source node type where attack originates
    private NodeType to;                       // Destination node type where attack leads
    private AttackType attackType;             // Category of attacker action being performed
    private String label;                     // Human-readable label describing the attacker action




    public AttackEdgeDefinition(
        NodeType from,                          
        NodeType to,                            
        AttackType attackType,                  
        String label                          
    ) {
        this.from = from;
        this.to = to;
        this.attackType = attackType;
        this.label = label;
    }


    // Returns the source node type of the attack edge
    public NodeType getFrom() {
        return from;
    }


    // Returns the destination node type of the attack edge
    public NodeType getTo() {
        return to;
    }


    // Returns the attack type associated with this edge
    public AttackType getAttackType() {
        return attackType;
    }


    // Returns a human-readable label for the attack
    public String getLabel() {
        return label;
    }
}


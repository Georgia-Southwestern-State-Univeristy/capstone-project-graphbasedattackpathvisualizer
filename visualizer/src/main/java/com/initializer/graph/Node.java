package com.initializer.graph;

// the Node class represents a single asset, system, account, or component in the graph
public class Node {
    
    private String id;                                      // unique identifier for the node
    private NodeType type;                                  // type of node (email, VPN, database, etc)

    public Node(String id, NodeType type) {                 // constructor for creating a node with an id and type (both required)
        this.id = id;
        this.type = type;
    }

    public String getId() {                                 // returns the unique id of the node
        return id;
    }

    public NodeType getType() {                             // returns the type of the node
        return type;
    }
}

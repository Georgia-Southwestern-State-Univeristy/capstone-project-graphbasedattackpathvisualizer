package com.initializer.graph;

public class Node {

    private String id;
    private NodeType type;
    private String displayName;

    public Node(String id, NodeType type, String displayName) {
        this.id = id;
        this.type = type;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public NodeType getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }
}

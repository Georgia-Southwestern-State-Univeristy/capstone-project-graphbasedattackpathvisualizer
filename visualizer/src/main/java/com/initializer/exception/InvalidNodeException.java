package com.initializer.exception;

// Thrown when a source or target node ID does not exist in the attack graph.

public class InvalidNodeException extends RuntimeException {

    public InvalidNodeException(String message) {
        super(message);
    }
}


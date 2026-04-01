package com.initializer.services;

// DTO representing a single recommended mitigation
// with explanation and priority.
public class MitigationDetailDTO {

    private String name;
    private String reason;
    private String priority;

    public MitigationDetailDTO() {
    }

    public MitigationDetailDTO(String name, String reason, String priority) {
        this.name = name;
        this.reason = reason;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}

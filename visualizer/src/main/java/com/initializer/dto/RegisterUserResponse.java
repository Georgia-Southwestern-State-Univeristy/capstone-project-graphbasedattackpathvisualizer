package com.initializer.dto;

import java.time.LocalDateTime;

public class RegisterUserResponse {

    private Integer userID;
    private String userEmail;
    private LocalDateTime userCreatedAt;
    private String message;

    public RegisterUserResponse(Integer userID,
                                String userEmail,
                                LocalDateTime userCreatedAt,
                                String message) {
        this.userID = userID;
        this.userEmail = userEmail;
        this.userCreatedAt = userCreatedAt;
        this.message = message;
    }

    public Integer getUserID() { return userID; }
    public String getUserEmail() { return userEmail; }
    public LocalDateTime getUserCreatedAt() { return userCreatedAt; }
    public String getMessage() { return message; }
}

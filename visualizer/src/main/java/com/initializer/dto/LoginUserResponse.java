package com.initializer.dto;

public class LoginUserResponse {

    private Integer userID;
    private String userEmail;
    private String message;

    public LoginUserResponse(Integer userID, String userEmail, String message) {
        this.userID = userID;
        this.userEmail = userEmail;
        this.message = message;
    }

    public Integer getUserID() { return userID; }
    public String getUserEmail() { return userEmail; }
    public String getMessage() { return message; }
}

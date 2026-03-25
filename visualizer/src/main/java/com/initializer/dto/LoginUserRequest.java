package com.initializer.dto;

public class LoginUserRequest {

    private String userEmail;
    private String password;

    public LoginUserRequest() {}

    public String getUserEmail() { return userEmail; }
    public String getPassword() { return password; }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

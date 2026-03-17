package com.initializer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userID;

    @Column(unique = true, nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String userPwHash;

    private LocalDateTime userCreatedAt;

    @OneToOne(mappedBy = "user")
    private BusinessProfileEntity businessProfile;

    public UserEntity() {}

    public UserEntity(String userEmail, String userPwHash) {
        this.userEmail = userEmail;
        this.userPwHash = userPwHash;
        this.userCreatedAt = LocalDateTime.now();
    }

    public Integer getUserID() { return userID; }

    public String getUserEmail() { return userEmail; }

    public String getUserPwHash() { return userPwHash; }

    public LocalDateTime getUserCreatedAt() { return userCreatedAt; }

    public BusinessProfileEntity getBusinessProfile() { return businessProfile; }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserPwHash(String userPwHash) {
        this.userPwHash = userPwHash;
    }

    public void setBusinessProfile(BusinessProfileEntity businessProfile) {
        this.businessProfile = businessProfile;
    }
}
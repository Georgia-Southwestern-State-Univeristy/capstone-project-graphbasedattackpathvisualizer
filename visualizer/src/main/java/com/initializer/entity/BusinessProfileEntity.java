package com.initializer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "businessprofile")
public class BusinessProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer profileID;

    private boolean usesVPN;

    private boolean hasFileServer;

    private boolean usesSaaS;

    private boolean hasPublicWebApp;

    private boolean usesIdentityProvider;

    public BusinessProfileEntity() {}

    public BusinessProfileEntity(boolean usesVPN,
                                 boolean hasFileServer,
                                 boolean usesSaaS,
                                 boolean hasPublicWebApp,
                                 boolean usesIdentityProvider) {
        this.usesVPN = usesVPN;
        this.hasFileServer = hasFileServer;
        this.usesSaaS = usesSaaS;
        this.hasPublicWebApp = hasPublicWebApp;
        this.usesIdentityProvider = usesIdentityProvider;
    }

    public Integer getProfileID() { return profileID; }

    public boolean isUsesVPN() { return usesVPN; }
    public boolean isHasFileServer() { return hasFileServer; }
    public boolean isUsesSaaS() { return usesSaaS; }
    public boolean isHasPublicWebApp() { return hasPublicWebApp; }
    public boolean isUsesIdentityProvider() { return usesIdentityProvider; }

    public void setUsesVPN(boolean usesVPN) { this.usesVPN = usesVPN; }
    public void setHasFileServer(boolean hasFileServer) { this.hasFileServer = hasFileServer; }
    public void setUsesSaaS(boolean usesSaaS) { this.usesSaaS = usesSaaS; }
    public void setHasPublicWebApp(boolean hasPublicWebApp) { this.hasPublicWebApp = hasPublicWebApp; }
    public void setUsesIdentityProvider(boolean usesIdentityProvider) {
        this.usesIdentityProvider = usesIdentityProvider;
    }
}

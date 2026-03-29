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

    private boolean hasEmailServer;
    private boolean hasDomainController;
    private boolean hasInternalApp;
    private boolean hasHRSystem;
    private boolean hasFinanceSystem;
    private boolean hasBackupServer;
    private boolean hasMDMServer;
    private boolean hasWirelessAccessPoint;
    private boolean hasFirewall;
    private boolean hasDNSServer;

    @OneToOne
    @JoinColumn(name = "userID", unique = true)
    private UserEntity user;

    public BusinessProfileEntity() {}

    public BusinessProfileEntity(boolean usesVPN,
                                 boolean hasFileServer,
                                 boolean usesSaaS,
                                 boolean hasPublicWebApp,
                                 boolean usesIdentityProvider,
                                 boolean hasEmailServer,
                                 boolean hasDomainController,
                                 boolean hasInternalApp,
                                 boolean hasHRSystem,
                                 boolean hasFinanceSystem,
                                 boolean hasBackupServer,
                                 boolean hasMDMServer,
                                 boolean hasWirelessAccessPoint,
                                 boolean hasFirewall,
                                 boolean hasDNSServer) {
        this.usesVPN = usesVPN;
        this.hasFileServer = hasFileServer;
        this.usesSaaS = usesSaaS;
        this.hasPublicWebApp = hasPublicWebApp;
        this.usesIdentityProvider = usesIdentityProvider;
        this.hasEmailServer = hasEmailServer;
        this.hasDomainController = hasDomainController;
        this.hasInternalApp = hasInternalApp;
        this.hasHRSystem = hasHRSystem;
        this.hasFinanceSystem = hasFinanceSystem;
        this.hasBackupServer = hasBackupServer;
        this.hasMDMServer = hasMDMServer;
        this.hasWirelessAccessPoint = hasWirelessAccessPoint;
        this.hasFirewall = hasFirewall;
        this.hasDNSServer = hasDNSServer;
    }

    public Integer getProfileID() { return profileID; }

    public boolean isUsesVPN() { return usesVPN; }
    public boolean isHasFileServer() { return hasFileServer; }
    public boolean isUsesSaaS() { return usesSaaS; }
    public boolean isHasPublicWebApp() { return hasPublicWebApp; }
    public boolean isUsesIdentityProvider() { return usesIdentityProvider; }

    public boolean isHasEmailServer() { return hasEmailServer; }
    public boolean isHasDomainController() { return hasDomainController; }
    public boolean isHasInternalApp() { return hasInternalApp; }
    public boolean isHasHRSystem() { return hasHRSystem; }
    public boolean isHasFinanceSystem() { return hasFinanceSystem; }
    public boolean isHasBackupServer() { return hasBackupServer; }
    public boolean isHasMDMServer() { return hasMDMServer; }
    public boolean isHasWirelessAccessPoint() { return hasWirelessAccessPoint; }
    public boolean isHasFirewall() { return hasFirewall; }
    public boolean isHasDNSServer() { return hasDNSServer; }

    public UserEntity getUser() { return user; }

    public void setUsesVPN(boolean usesVPN) { this.usesVPN = usesVPN; }
    public void setHasFileServer(boolean hasFileServer) { this.hasFileServer = hasFileServer; }
    public void setUsesSaaS(boolean usesSaaS) { this.usesSaaS = usesSaaS; }
    public void setHasPublicWebApp(boolean hasPublicWebApp) { this.hasPublicWebApp = hasPublicWebApp; }
    public void setUsesIdentityProvider(boolean usesIdentityProvider) { this.usesIdentityProvider = usesIdentityProvider; }

    public void setHasEmailServer(boolean hasEmailServer) { this.hasEmailServer = hasEmailServer; }
    public void setHasDomainController(boolean hasDomainController) { this.hasDomainController = hasDomainController; }
    public void setHasInternalApp(boolean hasInternalApp) { this.hasInternalApp = hasInternalApp; }
    public void setHasHRSystem(boolean hasHRSystem) { this.hasHRSystem = hasHRSystem; }
    public void setHasFinanceSystem(boolean hasFinanceSystem) { this.hasFinanceSystem = hasFinanceSystem; }
    public void setHasBackupServer(boolean hasBackupServer) { this.hasBackupServer = hasBackupServer; }
    public void setHasMDMServer(boolean hasMDMServer) { this.hasMDMServer = hasMDMServer; }
    public void setHasWirelessAccessPoint(boolean hasWirelessAccessPoint) { this.hasWirelessAccessPoint = hasWirelessAccessPoint; }
    public void setHasFirewall(boolean hasFirewall) { this.hasFirewall = hasFirewall; }
    public void setHasDNSServer(boolean hasDNSServer) { this.hasDNSServer = hasDNSServer; }

    public void setUser(UserEntity user) { this.user = user; }
}
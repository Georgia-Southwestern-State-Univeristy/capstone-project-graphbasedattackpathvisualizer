package com.initializer.services;

public class BusinessProfileDTO {

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

    public BusinessProfileDTO(Integer profileID,
                              boolean usesVPN,
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
        this.profileID = profileID;
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
}

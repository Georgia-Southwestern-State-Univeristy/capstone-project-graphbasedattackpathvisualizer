package com.initializer.services;

public class BusinessProfileDTO {

    private Integer profileID;
    private boolean usesVPN;
    private boolean hasFileServer;
    private boolean usesSaaS;
    private boolean hasPublicWebApp;
    private boolean usesIdentityProvider;

    public BusinessProfileDTO(Integer profileID,
                              boolean usesVPN,
                              boolean hasFileServer,
                              boolean usesSaaS,
                              boolean hasPublicWebApp,
                              boolean usesIdentityProvider) {
        this.profileID = profileID;
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
}

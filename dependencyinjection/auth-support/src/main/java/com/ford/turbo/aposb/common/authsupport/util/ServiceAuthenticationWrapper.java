package com.ford.turbo.aposb.common.authsupport.util;

public class ServiceAuthenticationWrapper {
    public enum AuthenticationMethods {
        OAUTH2,FIG
    }

    private AuthenticationMethods authenticationMethod;

    private String oauthBearerToken;
    private String applicationId;
    private String figAuthorizationToken;

    public ServiceAuthenticationWrapper(AuthenticationMethods authenticationMethod) {
             this.authenticationMethod = authenticationMethod;
    }

    public AuthenticationMethods getAuthenticationMethod() {
        return authenticationMethod;
    }

    public void setAuthenticationMethod(AuthenticationMethods authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    public String getOauthBearerToken() {
        return oauthBearerToken;
    }

    public void setOauthBearerToken(String oauthBearerToken) {
        if(!this.authenticationMethod.equals(AuthenticationMethods.OAUTH2))
            throw new IllegalStateException("Unable to set oauth token when authentication method is not oauth. Current auth method is  "+this.getAuthenticationMethod().toString());

        this.oauthBearerToken = oauthBearerToken;
    }

    public void setApplicationId(String figApplicationId) {
        this.applicationId = figApplicationId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setFigAuthorizationToken(String figAuthorizationToken) {
        if(!this.authenticationMethod.equals(AuthenticationMethods.FIG))
            throw new IllegalStateException("Unable to set auth token when authentication method is not FIG. Current auth method is  "+this.getAuthenticationMethod().toString());

        this.figAuthorizationToken = figAuthorizationToken;
    }

    public String getFigAuthorizationToken() {
        return figAuthorizationToken;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ServiceAuthenticationWrapper)) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        ServiceAuthenticationWrapper other = (ServiceAuthenticationWrapper) obj;

        if (authenticationMethod != other.getAuthenticationMethod()) {
            return false;
        }

        if (oauthBearerToken != null ) {
            if (!oauthBearerToken.equals(other.getOauthBearerToken())) {
                return false;
            }
        } else {
            if (other.getOauthBearerToken() != null) {
                return false;
            }
        }

        if (applicationId != null) {
            if (!applicationId.equals(other.getApplicationId())) {
                return false;
            }
        } else if  (other.getApplicationId() != null) {
            return false;
        }

        if (figAuthorizationToken != null) {
            if (!figAuthorizationToken.equals(other.getFigAuthorizationToken())) {
                return false;
            }
        } else if (other.getFigAuthorizationToken() != null) {
            return false;
        }

        return true;
    }
}

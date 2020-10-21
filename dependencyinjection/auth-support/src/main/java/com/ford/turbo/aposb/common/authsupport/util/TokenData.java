package com.ford.turbo.aposb.common.authsupport.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenData {

    public String DisplayName;
    public String UserId;
    public String ExternalToken;
    public String SessionId;
    public String DateIssued;
    public String Issuer;
    public String Validated;
    public String Version;
    public String TTL;
    public String UniqueAuthId;
    public String SdnAppId;
    public String DataCenter;

    public String getDataCenter() {
        return DataCenter;
    }

    public void setDataCenter(String dataCenter) {
        DataCenter = dataCenter;
    }

    public String getSdnAppId() {
        return SdnAppId;
    }

    public void setSdnAppId(String sdnAppId) {
        SdnAppId = sdnAppId;
    }

    public String getUniqueAuthId() {
        return UniqueAuthId;
    }

    public void setUniqueAuthId(String uniqueAuthId) {
        UniqueAuthId = uniqueAuthId;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getExternalToken() {
        return ExternalToken;
    }

    public void setExternalToken(String externalToken) {
        ExternalToken = externalToken;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String sessionId) {
        SessionId = sessionId;
    }

    public String getDateIssued() {
        return DateIssued;
    }

    public void setDateIssued(String dateIssued) {
        DateIssued = dateIssued;
    }

    public String getIssuer() {
        return Issuer;
    }

    public void setIssuer(String issuer) {
        Issuer = issuer;
    }

    public String getValidated() {
        return Validated;
    }

    public void setValidated(String validated) {
        Validated = validated;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public String getTTL() {
        return TTL;
    }

    public void setTTL(String TTL) {
        this.TTL = TTL;
    }
}

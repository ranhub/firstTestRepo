package com.ford.turbo.aposb.common.authsupport.environment.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProvidedService {

    @JsonProperty("name")
    String name;

    @JsonProperty("label")
    String label;

    @JsonProperty("tags")
    List<String> tags;

    @JsonProperty("credentials")
    Map<String, Object> credentials;

    @JsonProperty("syslog_drain_url")
    String syslogDrainUrl;

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public List<String> getTags() {
        return tags;
    }

    public Map<String, Object> getCredentials() {
        return credentials;
    }

    public String getSyslogDrainUrl() {
        return syslogDrainUrl;
    }
}

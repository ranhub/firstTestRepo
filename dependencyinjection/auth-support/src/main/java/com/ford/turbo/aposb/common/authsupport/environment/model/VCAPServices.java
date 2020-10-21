package com.ford.turbo.aposb.common.authsupport.environment.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class VCAPServices {

    @JsonProperty(value = "user-provided")
    List<UserProvidedService> userProvided;

    public UserProvidedService getUserProvidedService(String serviceName) {
        log.info("Resolving user-provided service " + serviceName + "...");
        return userProvided.stream()
                .filter(userProvidedService -> userProvidedService.getName().equals(serviceName))
                .findAny().get();
    }
}

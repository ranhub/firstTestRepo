package com.ford.turbo.aposb.common.authsupport.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class SdnTokenUtility {

    public static String serializedSdnToken(String token, String appId) {
        try {
            ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
            SdnToken sdnToken = new SdnToken(token, appId);
            return objectWriter.writeValueAsString(sdnToken);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unexpected failure creating FIG request body", e);
        }
    }

}

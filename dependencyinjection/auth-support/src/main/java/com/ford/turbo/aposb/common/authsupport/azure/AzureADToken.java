package com.ford.turbo.aposb.common.authsupport.azure;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AzureADToken {
    final String bearerToken;
    final LocalDateTime expireDate;
}

package com.ford.turbo.aposb.common.authsupport.fordmapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.authsupport.models.ContinentCode;
import com.ford.turbo.aposb.common.authsupport.models.Make;

import lombok.Data;

@SuppressWarnings("unchecked")
public abstract class ApplicationIdMapping {

    protected CredentialsSource continentCredentials;
    protected Map<String, AppIdAttributes> attributesByAppId = new HashMap<>();

    public ApplicationIdMapping(CredentialsSource continentCredentials) {
        this.continentCredentials = continentCredentials;
        initAppIdMap();
    }

    protected void initAppIdMap() {
    	// get all possible continent codes & makees
		Map<String, ContinentCode> continentByCode = Stream.of(ContinentCode.values()).collect(Collectors.toMap(e -> e.name().toUpperCase(), Function.identity()));
		Map<String, Make> makeByCode = Stream.of(Make.values()).collect(Collectors.toMap(e -> e.name().toUpperCase(), Function.identity()));
		
		// structure:  {NA -> {Ford -> [..appIds...], Lincoln -> []}, AP -> {Ford -> [...], Lincoln -> "APP-ID"}, ....}
		for (Entry<String, Object> continentEntry : this.continentCredentials.getExtraCredentials().entrySet()) {
			ContinentCode continentCode = continentByCode.get(continentEntry.getKey().toUpperCase());  //ensure valid ContinentCode
			if (continentCode == null) continue;
			
			for (Entry<String, Object> makeEntry : ((HashMap<String, Object>)continentEntry.getValue()).entrySet()) {
				Make make = makeByCode.get(makeEntry.getKey().toUpperCase());  //ensure valid Make
				if (make == null) continue;
				
				addAppIds(makeEntry.getValue(), continentCode, make);
			}
		}   	
	}

	private void addAppIds(Object appIds, ContinentCode continentCode, Make make) {
		List<String> appIdList = appIds instanceof List<?> ? (List<String>)appIds : Arrays.asList((String)appIds);
		
		for (String appId : appIdList) {
			if (appId != null && !appId.trim().isEmpty()) {
				attributesByAppId.put(appId, new AppIdAttributes(appId, continentCode, make));
			}
		}
	}
    
    @Data
    public static class AppIdAttributes {
    	final String appId;
    	final ContinentCode continentCode;
    	final Make make;
    }
}

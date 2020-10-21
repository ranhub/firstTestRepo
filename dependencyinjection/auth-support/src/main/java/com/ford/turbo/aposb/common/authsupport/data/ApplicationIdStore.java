package com.ford.turbo.aposb.common.authsupport.data;

import static com.ford.turbo.aposb.common.authsupport.data.ApplicationId.Classifier.AUTHENTICATED;
import static com.ford.turbo.aposb.common.authsupport.data.ApplicationId.Classifier.UNAUTHENTICATED;
import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.authsupport.fordmapping.ApplicationIdMapping;
import com.ford.turbo.aposb.common.authsupport.models.ContinentCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ford.turbo.aposb.common.authsupport.data.ApplicationId.Classifier;

@Component
public class ApplicationIdStore extends ApplicationIdMapping {
	private Map<String, ApplicationId> applicationIds = new HashMap<>();

	@Autowired
	public ApplicationIdStore(@Qualifier("APPLICATION_ID_MAPPINGS") CredentialsSource appIds,
			@Autowired(required = false) @Qualifier("PUBLIC_APP_ID_MAPPINGS") CredentialsSource publicAppIds) {
		super(appIds);
		translate(this.attributesByAppId);
		translate(publicAppIds);
	}

	private void translate(Map<String, AppIdAttributes> appIds) {
		this.attributesByAppId.forEach((key, value) -> {
			this.applicationIds.put(key, new ApplicationId(key, value.getContinentCode(), AUTHENTICATED));
		});
	}

	@SuppressWarnings("unchecked")
	private void translate(CredentialsSource publicAppIds) {
		if (publicAppIds == null) {
			return;
		}
		for (Entry<String, Object> entry : publicAppIds.getExtraCredentials().entrySet()) {
			((List<String>) entry.getValue()).forEach(appId -> {
				this.applicationIds.put(appId,
						new ApplicationId(appId, ContinentCode.valueOf(entry.getKey()), UNAUTHENTICATED));
			});
		}
	}

	public Optional<ApplicationId> getApplicationId(String appId) {
		return Optional.ofNullable(this.applicationIds.get(appId));
	}
	
	public Optional<ContinentCode> getContinentCode(String appId) {
		ApplicationId applicationId = this.applicationIds.get(appId);
		return applicationId == null ? empty() : of(applicationId.getRegion());
	}

	public Optional<ContinentCode> getContinentCode(String appId, Classifier... inClassifiers) {
		ApplicationId applicationId = this.applicationIds.get(appId);
		return applicationId != null
				&& stream(inClassifiers).anyMatch(classifier -> applicationId.getClassifier() == classifier)
						? of(applicationId.getRegion()) : empty();
	}
}

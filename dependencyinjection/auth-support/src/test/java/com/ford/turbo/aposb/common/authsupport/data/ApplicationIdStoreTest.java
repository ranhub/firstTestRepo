package com.ford.turbo.aposb.common.authsupport.data;

import static com.ford.turbo.aposb.common.authsupport.data.ApplicationId.Classifier.AUTHENTICATED;
import static com.ford.turbo.aposb.common.authsupport.data.ApplicationId.Classifier.UNAUTHENTICATED;
import static com.ford.turbo.aposb.common.authsupport.models.ContinentCode.EU;
import static com.ford.turbo.aposb.common.authsupport.models.ContinentCode.NA;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;

public class ApplicationIdStoreTest {
	private static CredentialsSource APP_IDS;
	private static CredentialsSource PUBLIC_APP_IDS;

	private static ApplicationIdStore STORE;

	@BeforeClass
	public static void beforeClass() throws IOException {
		final String vcap = IOUtils.toString(new ClassPathResource("VCAP_PUBLIC_AND_NON_PUBLIC_APP_ID_MAPPINGS").getURI());
		APP_IDS = initCredentials("APPLICATION_ID_MAPPINGS", vcap);
		PUBLIC_APP_IDS = initCredentials("PUBLIC_APP_ID_MAPPINGS", vcap);
		STORE = new ApplicationIdStore(APP_IDS, PUBLIC_APP_IDS);
	}
	
	@Test
	public void getsNothingForInvalidApplicationId() {
		assertThat(STORE.getApplicationId("some-non-existing").isPresent()).isFalse();
	}
	
	@Test
	public void getsApplicationIdForValidApplicationId() {
		assertThat(STORE.getApplicationId("APPID-NA-FORD-1").get().getId()).isEqualTo("APPID-NA-FORD-1");
	}

	@Test
	public void getsNothingWhenApplicationIdIsNull() {
		assertThat(STORE.getContinentCode(null).isPresent()).isFalse();
	}

	@Test
	public void getsNothingWhenApplicationIdIsNullWithFilters() {
		assertThat(STORE.getContinentCode(null, UNAUTHENTICATED).isPresent()).isFalse();
	}

	@Test
	public void getsContinentCodeOfUnauthenticatedApplicationId() {
		assertThat(STORE.getContinentCode("eu-app-id2").get()).isEqualTo(EU);
	}

	@Test
	public void getsContinentCodeOfAuthenticatedApplicationId() {
		assertThat(STORE.getContinentCode("APPID-NA-FORD-1").get()).isEqualTo(NA);
	}

	@Test
	public void getsNothingWhenFilterByClassifierDoesNotYieldAnApplicationId() throws Exception {
		assertThat(STORE.getContinentCode("APPID-NA-FORD-1", UNAUTHENTICATED).isPresent()).isFalse();
	}

	@Test
	public void getsNothingWhenAppIdIsNotFound() throws Exception {
		assertThat(STORE.getContinentCode("APPID-NA-NOT-FOUND").isPresent()).isFalse();
	}

	@Test
	public void getsContinentCodeFromUnathenticatedApplicationIdWithFilter() throws Exception {
		assertThat(STORE.getContinentCode("APPID-NA-FORD-1", AUTHENTICATED).get()).isEqualTo(NA);
	}

	@Test
	public void getsContinentCodeFromUnathenticatedApplicationIdWithFilters() throws Exception {
		assertThat(STORE.getContinentCode("APPID-NA-FORD-1", AUTHENTICATED, UNAUTHENTICATED).isPresent()).isTrue();
	}

	@Test
	public void getsContinentCodeFromAuthenticatedApplicatonIdWithFilter() throws Exception {
		assertThat(STORE.getContinentCode("eu-app-id1", UNAUTHENTICATED).get()).isEqualTo(EU);
	}

	@Test
	public void getsContinentCodeFromAuthenticatedApplicatonIdWithFilters() throws Exception {
		assertThat(STORE.getContinentCode("eu-app-id1", AUTHENTICATED, UNAUTHENTICATED).get()).isEqualTo(EU);
	}

	@Test
	public void getsContinentCodeFromPublicAppIdMappingsWithDuplicateClassifier() throws Exception {
		assertThat(STORE.getContinentCode("eu-app-id1", UNAUTHENTICATED, UNAUTHENTICATED).get()).isEqualTo(EU);
	}

	private static CredentialsSource initCredentials(String name, String vcap) throws IOException {
		return new CredentialsSource(name) {
			@Override
			protected String getVCAPServicesEnvValue() {
				return vcap;
			}
		};
	}
}
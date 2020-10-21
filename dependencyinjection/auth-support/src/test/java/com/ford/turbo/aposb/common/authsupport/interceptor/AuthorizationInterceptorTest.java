package com.ford.turbo.aposb.common.authsupport.interceptor;

import static com.ford.turbo.aposb.common.authsupport.data.ApplicationId.Classifier.UNAUTHENTICATED;
import static com.ford.turbo.aposb.common.authsupport.data.ApplicationId.Classifier.AUTHENTICATED;
import static com.ford.turbo.aposb.common.authsupport.models.ContinentCode.AP;
import static com.ford.turbo.aposb.common.authsupport.models.ContinentCode.EU;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import com.ford.turbo.aposb.common.authsupport.annotation.Authorization;
import com.ford.turbo.aposb.common.authsupport.data.ApplicationId;
import com.ford.turbo.aposb.common.authsupport.data.ApplicationIdStore;
import com.ford.turbo.aposb.common.authsupport.models.ContinentCode;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationInterceptorTest {
	@InjectMocks
	private AuthorizationInterceptor interceptor;
	@Mock
	private ApplicationIdStore applicationIdStore;
	@Mock
	private HttpServletRequest request;
	private MockHttpServletResponse response = new MockHttpServletResponse();

	@Before
	public void each() {
		doReturn("app-id").when(this.request).getHeader("Application-Id");
	}

	@Test
	public void blocksAndResponds3001WhenAuthorizationPolicyIsNotDefined() throws Exception {
		boolean actual = interceptor.preHandle(this.request, this.response,
				method(AuthorizationLaboratory1.class, "noPolicy"));
		assertUnauthorizedResponse(actual);
	}

	@Test
	public void blocksAndResponds3001ForUnauthenticatedRequestWhenAppIdIsNotAuthorized() throws Exception {
		doReturn(Optional.of(new ApplicationId("app-id", ContinentCode.SA, UNAUTHENTICATED))).when(this.applicationIdStore).getApplicationId(anyString());
		boolean actual = interceptor.preHandle(this.request, this.response,
				method(AuthorizationLaboratory.class, "inheritClassPolicy"));
		assertUnauthorizedResponse(actual);
	}
	
	@Test
	public void blocksAndResponds3001ForAuthenticatedRequestWhenAppIdIsNotAuthorized() throws Exception {
		doReturn(Optional.of(new ApplicationId("app-id", ContinentCode.SA, AUTHENTICATED))).when(this.applicationIdStore).getApplicationId(anyString());
		boolean actual = interceptor.preHandle(this.request, this.response,
				method(AuthorizationLaboratory.class, "authenticatedRequest"));
		assertUnauthorizedResponse(actual);
	}

	@Test
	public void allowsRequestWhenAppIdIsAuthorizedOnOverridenPolicy() throws Exception {
		doReturn(Optional.of(new ApplicationId("app-id", ContinentCode.EU, UNAUTHENTICATED))).when(this.applicationIdStore).getApplicationId(anyString());
		boolean actual = interceptor.preHandle(this.request, this.response,
				method(AuthorizationLaboratory.class, "allowRequestByOverridingPolicy"));
		assertAuthorizedResponse(actual);
	}

	@Test
	public void allowsRequestWhenAppIdIsAuthorized() throws Exception {
		doReturn(Optional.of(new ApplicationId("app-id", ContinentCode.AP, UNAUTHENTICATED))).when(this.applicationIdStore).getApplicationId(anyString());
		boolean actual = interceptor.preHandle(this.request, this.response,
				method(AuthorizationLaboratory.class, "allowRequest"));
		assertAuthorizedResponse(actual);
	}
	
	@Test
	public void allowsRequestWhenAppIdIsAuthorizedForAuthenticatedRequests() throws Exception {
		doReturn(Optional.of(new ApplicationId("app-id", ContinentCode.AP, AUTHENTICATED))).when(this.applicationIdStore).getApplicationId(anyString());
		boolean actual = interceptor.preHandle(this.request, this.response,
				method(AuthorizationLaboratory.class, "authenticatedRequest"));
		assertAuthorizedResponse(actual);
	}

	@Test
	public void blocksAndResponds400WhenApplicationIdHeaderIsNotPresent() throws Exception {
		doReturn(null).when(this.request).getHeader("Application-Id");
		boolean actual = interceptor.preHandle(this.request, this.response,
				method(AuthorizationLaboratory.class, "inheritClassPolicy"));
		assertMissingAppIdResponse(actual);
	}

	@Test
	public void blocksAndResponds403WhenApplicationIdHeaderIsInvalid() throws Exception {
		doReturn(Optional.empty()).when(this.applicationIdStore).getApplicationId(anyString());
		boolean actual = interceptor.preHandle(this.request, this.response,
				method(AuthorizationLaboratory.class, "inheritClassPolicy"));
		assertInvalidAppIdResponse(actual);
	}

	private <T> HandlerMethod method(Class<T> clazz, String name) throws NoSuchMethodException, SecurityException {
		HandlerMethod method = Mockito.mock(HandlerMethod.class);
		doReturn(clazz.getMethod(name)).when(method).getMethod();
		return method;
	}

	private void assertAuthorizedResponse(boolean actual) throws UnsupportedEncodingException {
		assertThat(actual).isTrue();
		assertThat(this.response.getContentAsString()).isEmpty();
		assertThat(this.response.getStatus()).isEqualTo(200);
	}

	private void assertUnauthorizedResponse(boolean actual) throws UnsupportedEncodingException {
		assertThat(actual).isFalse();
		assertThat(this.response.getContentAsString()).isEqualTo(
				"{\"requestStatus\":\"UNAVAILABLE\",\"error\":{\"statusContext\":\"Marketing Services Layer\",\"statusCode\":3001,\"message\":\"Service is not configured for the request Application ID\"},\"lastRequested\":null}");
		assertThat(this.response.getContentType()).isEqualTo("application/json");
		assertThat(this.response.getStatus()).isEqualTo(200);
	}

	private void assertMissingAppIdResponse(boolean actual) throws UnsupportedEncodingException {
		assertThat(actual).isFalse();
		assertThat(this.response.getContentAsString()).isEqualTo(
				"{\"requestStatus\":\"UNAVAILABLE\",\"error\":{\"statusContext\":\"HTTP\",\"statusCode\":400,\"message\":\"Authorization has been denied for this request. App Id could be missing.\"},\"lastRequested\":null}");
		assertThat(this.response.getContentType()).isEqualTo("application/json");
		assertThat(this.response.getStatus()).isEqualTo(400);
	}

	private void assertInvalidAppIdResponse(boolean actual) throws UnsupportedEncodingException {
		assertThat(actual).isFalse();
		assertThat(this.response.getContentAsString()).isEqualTo(
				"{\"requestStatus\":\"UNAVAILABLE\",\"error\":{\"statusContext\":\"HTTP\",\"statusCode\":403,\"message\":\"Unknown application id app-id\"},\"lastRequested\":null}");
		assertThat(this.response.getContentType()).isEqualTo("application/json");
		assertThat(this.response.getStatus()).isEqualTo(403);
	}
}

@Authorization(authenticate = false, regions = { EU, AP })
class AuthorizationLaboratory {
	public void inheritClassPolicy() {
	}

	@Authorization(authenticate = false, regions = { EU, AP })
	public void allowRequestByOverridingPolicy() {
	}

	public void allowRequest() {
	}

	@Authorization(authenticate = true, regions = { EU, AP })
	public void authenticatedRequest() {
	}
}

class AuthorizationLaboratory1 {
	public void noPolicy() {
	}
}

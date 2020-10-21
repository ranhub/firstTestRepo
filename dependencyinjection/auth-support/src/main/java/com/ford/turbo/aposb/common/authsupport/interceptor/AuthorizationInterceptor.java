package com.ford.turbo.aposb.common.authsupport.interceptor;

import static com.ford.turbo.aposb.common.authsupport.data.ApplicationId.Classifier.AUTHENTICATED;
import static com.ford.turbo.aposb.common.authsupport.data.ApplicationId.Classifier.UNAUTHENTICATED;
import static java.util.Arrays.stream;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;
import com.ford.turbo.aposb.common.basemodels.model.FordError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.turbo.aposb.common.authsupport.annotation.Authorization;
import com.ford.turbo.aposb.common.authsupport.data.ApplicationId;
import com.ford.turbo.aposb.common.authsupport.data.ApplicationId.Classifier;
import com.ford.turbo.aposb.common.authsupport.data.ApplicationIdStore;
import com.ford.turbo.aposb.common.authsupport.models.ContinentCode;

@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {
	private final ObjectMapper mapper = new ObjectMapper();
	private final ApplicationIdStore applicationIdStore;

	@Autowired
	public AuthorizationInterceptor(ApplicationIdStore applicationIdStore) {
		this.applicationIdStore = applicationIdStore;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String incomingApplicationId = request.getHeader("Application-Id");
		if (StringUtils.isEmpty(incomingApplicationId)) {
			return missingAppId(response);
		}
		Authorization policy = policy(handler);
		if (policy == null) {
			return respond3001(response);
		}
		Optional<ApplicationId> applicationId = this.applicationIdStore.getApplicationId(request.getHeader("Application-Id"));
		boolean authorized;
		if (applicationId.isPresent()) {
			ContinentCode region = applicationId.get().getRegion();
			Classifier appIdClassifier = applicationId.get().getClassifier();
			Classifier policyClassifier = policy.authenticate() ? AUTHENTICATED : UNAUTHENTICATED;
			// if policyClassifier == appIdClassifier && policyClassifier == AUTHENTICATED, do auth-token validation here
			authorized = (appIdClassifier == policyClassifier) ? stream(policy.regions()).anyMatch(r -> r == region) : false;
		} else {
			return forbidden(response, incomingApplicationId);
		}
		return authorized ? authorized : respond3001(response);
	}

	private Boolean respond3001(HttpServletResponse response) throws IOException, JsonProcessingException {
		write(new FordError("Marketing Services Layer", 3001,
				"Service is not configured for the request Application ID"), OK.value(), response);
		return false;
	}

	private Boolean missingAppId(HttpServletResponse response) throws IOException, JsonProcessingException {
		write(new FordError("HTTP", 400, "Authorization has been denied for this request. App Id could be missing."),
				BAD_REQUEST.value(), response);
		return false;
	}

	private Boolean forbidden(HttpServletResponse response, String applicationId)
			throws IOException, JsonProcessingException {
		write(new FordError("HTTP", 403, "Unknown application id " + applicationId), FORBIDDEN.value(), response);
		return false;
	}

	private void write(FordError error, int status, HttpServletResponse response)
			throws JsonProcessingException, IOException {
		BaseResponse body = new BaseResponse();
		body.setLastRequested(null);
		body.setRequestStatus(BaseResponse.RequestStatus.UNAVAILABLE);
		body.setError(error);
		response.setStatus(status);
		response.setContentType(APPLICATION_JSON_VALUE);
		response.getWriter().write(this.mapper.writeValueAsString(body));
		response.getWriter().flush();
	}

	private Authorization policy(Object handler) {
		Method method = ((HandlerMethod) handler).getMethod();
		Authorization annotation = method.getAnnotation(Authorization.class);
		Authorization authorization = annotation != null ? annotation
				: method.getDeclaringClass().getAnnotation(Authorization.class);
		return authorization;
	}
}
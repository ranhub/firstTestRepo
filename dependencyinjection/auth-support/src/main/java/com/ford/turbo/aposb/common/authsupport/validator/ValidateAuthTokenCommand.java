package com.ford.turbo.aposb.common.authsupport.validator;

import com.ford.turbo.aposb.common.basemodels.controller.exception.AuthTokenMalformedException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.AuthTokenNotAuthorizedException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.AuthTokenNotFoundException;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.aposb.common.authsupport.models.UserIdentity;
import com.netflix.hystrix.HystrixCommandGroupKey;

public abstract class ValidateAuthTokenCommand extends TimedHystrixCommand<UserIdentity> {

    public ValidateAuthTokenCommand(TraceInfo traceInfo) {
        super(traceInfo, Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("FIG")));
    }

    public ValidateAuthTokenCommand(TraceInfo traceInfo, String groupKey) {
        super(traceInfo, Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey)));
    }

    /**
     * Invokes {@link #performValidation()}.
     */
    @Override
    public final UserIdentity doRun() throws Exception {
        return performValidation();
    }

    /**
     * Validates the auth token that was previously set by a call to {@link #setAuthToken(String)}
     * throwing a subtype of HystrixBadRequestException if the auth token is not valid, and throwing some other exception
     * type if the token validation fails for an unexpected reason.
     *
     * @return the authenticated user's Lighthouse GUID.
     * @throws AuthTokenNotFoundException if the provided auth token is empty or null
     * @throws AuthTokenMalformedException if the provided auth token is not well-formed (eg. it has been tampered with)
     * @throws AuthTokenNotAuthorizedException if the auth token is no longer valid (eg. it has expired or the user has logged out)
     */
    public abstract UserIdentity performValidation();

    /**
     * FIXME This method should be deleted and its argument should be passed directly to {@link #performValidation()}.
     */
    public abstract void setAuthToken(String authToken);

    public abstract void setAppId(String appId);
}

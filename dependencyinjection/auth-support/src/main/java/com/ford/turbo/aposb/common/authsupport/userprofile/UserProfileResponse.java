package com.ford.turbo.aposb.common.authsupport.userprofile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ford.turbo.aposb.common.basemodels.model.CSDNError;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private String $id;
    private UserProfile profile;
    private Integer status;
    private String version;
    @JsonInclude(JsonInclude.Include.NON_NULL)
	private CSDNError error;

    public String get$id() {
        return $id;
    }

    public void set$id(String $id) {
        this.$id = $id;
    }

}

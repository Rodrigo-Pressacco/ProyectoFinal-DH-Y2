package com.dhmoney.userservice.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse {

    private String accessToken;
    private Long expires_in;
    private String refresh_expires_in;
    private String refresh_token;
    private String token_type;
    @JsonProperty("not-before-policy")
    private String not_before_policy;
    private String session_state;
    private String scope;

}

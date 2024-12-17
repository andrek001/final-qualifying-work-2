package com.netology.diplom.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Login {
    @JsonProperty("auth-token")
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    private String authToken ;
}

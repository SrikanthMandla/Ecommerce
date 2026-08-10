package com.ecommerce.project.security.response;

import java.util.List;

public class UserInfoResponse {

    private Long id;

    private String  username;
    private List<String> roles;
    private String jwtToken;

    public UserInfoResponse(Long id,  String username, List<String> roles   ) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public UserInfoResponse(Long id,String username, List<String> roles,String jwtToken) {
        this.id = id;
        this.jwtToken = jwtToken;
        this.roles = roles;
        this.username = username;
    }

    public UserInfoResponse setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserInfoResponse setUsername(String username) {
        this.username = username;
        return this;
    }

    public List<String> getRoles() {
        return roles;
    }

    public UserInfoResponse setRoles(List<String> roles) {
        this.roles = roles;
        return this;
    }

    public Long getId() {
        return id;
    }

    public UserInfoResponse setId(Long id) {
        this.id = id;
        return this;
    }
}

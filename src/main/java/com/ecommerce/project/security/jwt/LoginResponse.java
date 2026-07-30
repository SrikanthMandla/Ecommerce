package com.ecommerce.project.security.jwt;

import java.util.List;

public class LoginResponse {
    private String jwtToken;

    private String  username;
    private List<String> roles;

    public String getJwtToken() {
        return jwtToken;
    }

    public LoginResponse(String jwtToken, List<String> roles, String username) {
        this.jwtToken = jwtToken;
        this.roles = roles;
        this.username = username;
    }

    public LoginResponse setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public LoginResponse setUsername(String username) {
        this.username = username;
        return this;
    }

    public List<String> getRoles() {
        return roles;
    }

    public LoginResponse setRoles(List<String> roles) {
        this.roles = roles;
        return this;
    }
}

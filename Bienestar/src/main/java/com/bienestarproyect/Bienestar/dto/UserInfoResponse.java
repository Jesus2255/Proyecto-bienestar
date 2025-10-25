package com.bienestarproyect.Bienestar.dto;

public class UserInfoResponse {
    private boolean success;
    private String username;
    private String role;

    public UserInfoResponse() {}

    public UserInfoResponse(boolean success, String username, String role) {
        this.success = success;
        this.username = username;
        this.role = role;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

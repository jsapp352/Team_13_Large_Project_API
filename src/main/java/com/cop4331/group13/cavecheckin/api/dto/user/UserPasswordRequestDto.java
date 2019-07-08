package com.cop4331.group13.cavecheckin.api.dto.user;

public class UserPasswordRequestDto {
    private String newPassword;

    public UserPasswordRequestDto() {
    }

    public UserPasswordRequestDto(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

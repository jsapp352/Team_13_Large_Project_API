package com.cop4331.group13.cavecheckin.api.dto.user;

public class UserByPinRequestDto {
    String encryptedPin;

    public UserByPinRequestDto(String encryptedPin) {
        this.encryptedPin = encryptedPin;
    }

    public UserByPinRequestDto() {

    }

    public String getEncryptedPin() {
        return encryptedPin;
    }

    public void setEncryptedPin(String encryptedPin) {
        this.encryptedPin = encryptedPin;
    }
}

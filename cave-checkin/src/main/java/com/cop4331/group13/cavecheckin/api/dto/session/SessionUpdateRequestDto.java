package com.cop4331.group13.cavecheckin.api.dto.session;

public class SessionUpdateRequestDto {
    private String encryptedPin;
    private long sessionId;

    public SessionUpdateRequestDto() {}

    public SessionUpdateRequestDto(String encryptedPin, long sessionId) {
        this.encryptedPin = encryptedPin;
        this.sessionId = sessionId;
    }

    public String getEncryptedPin() {
        return encryptedPin;
    }

    public void setEncryptedPin(String encryptedPin) {
        this.encryptedPin = encryptedPin;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
}

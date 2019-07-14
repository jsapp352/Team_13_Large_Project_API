package com.cop4331.group13.cavecheckin.api.dto.session;

import java.util.List;

public class SessionHistoryResponseDto {
    public SessionHistoryResponseDto(List<SessionResponseDto> sessions, int averageSessionDuration) {
        this.sessions = sessions;
        this.averageSessionDuration = averageSessionDuration;
    }

    public SessionHistoryResponseDto() {
    }

    private List<SessionResponseDto> sessions;
    private int averageSessionDuration;

    public List<SessionResponseDto> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionResponseDto> sessions) {
        this.sessions = sessions;
    }

    public int getAverageSessionDuration() {
        return averageSessionDuration;
    }

    public void setAverageSessionDuration(int averageSessionDuration) {
        this.averageSessionDuration = averageSessionDuration;
    }
}

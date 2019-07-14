package com.cop4331.group13.cavecheckin.api.dto.session;

public class SessionHistoryRequestDto {
    private long courseId;
    private long taId;
    private long userId;

    public SessionHistoryRequestDto(long courseId, long taId, long userId) {
        this.courseId = courseId;
        this.taId = taId;
        this.userId = userId;
    }

    public SessionHistoryRequestDto () {
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getTaId() {
        return taId;
    }

    public void setTaId(long taId) {
        this.taId = taId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}

package com.cop4331.group13.cavecheckin.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TaCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long taCourseId;
    private long userId;
    private long courseId;
    private boolean isActive;

    public TaCourse() {
    }

    public TaCourse(long taCourseId, long userId, long courseId, boolean isActive) {
        this.taCourseId = taCourseId;
        this.userId = userId;
        this.courseId = courseId;
        this.isActive = isActive;
    }

    public long getTaCourseId() {
        return taCourseId;
    }

    public void setTaCourseId(long taCourseId) {
        this.taCourseId = taCourseId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

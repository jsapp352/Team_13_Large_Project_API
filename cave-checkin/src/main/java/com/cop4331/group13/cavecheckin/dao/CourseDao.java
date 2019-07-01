package com.cop4331.group13.cavecheckin.dao;

import com.cop4331.group13.cavecheckin.domain.Course;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CourseDao extends CrudRepository<Course, Long> {

    List<Course> findByUserId(long userId);
}

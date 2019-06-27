package com.cop4331.group13.cavecheckin.dao;

import com.cop4331.group13.cavecheckin.domain.Course;
import org.springframework.data.repository.CrudRepository;

public interface CourseDao extends CrudRepository<Course, Long> {
}

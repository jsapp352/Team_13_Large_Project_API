package com.cop4331.group13.cavecheckin.dao;

import com.cop4331.group13.cavecheckin.domain.TaCourse;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TaCourseDao extends CrudRepository<TaCourse, Long> {
    List<TaCourse> findByUserId(long userId);

    TaCourse findByUserIdAndCourseId(long userId, long courseId);
}

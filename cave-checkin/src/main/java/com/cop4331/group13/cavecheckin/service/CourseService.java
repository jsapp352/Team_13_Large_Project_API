package com.cop4331.group13.cavecheckin.service;

import com.cop4331.group13.cavecheckin.dao.CourseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    @Autowired
    CourseDao courseDao;
}

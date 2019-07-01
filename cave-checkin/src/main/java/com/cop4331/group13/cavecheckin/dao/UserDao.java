package com.cop4331.group13.cavecheckin.dao;

import com.cop4331.group13.cavecheckin.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserDao extends CrudRepository<User, Long> {

    User findByUsername(String username);

    List<User> findByRole(String role);

    User findByKioskPin(String kioskPin);

    @Query("SELECT u.kioskPin FROM User u")
    List<String> findAllKioskPins();

    @Query("SELECT u FROM User u, TaCourse t WHERE u.userId = t.userId AND t.courseId = :courseId AND u.isActive = true AND t.isActive = true")
    List<User> findUsersByCourseId(long courseId);

    @Query("SELECT u.userId FROM User u WHERE u.username = :username")
    Long findUserIdByUsername(String username);
}

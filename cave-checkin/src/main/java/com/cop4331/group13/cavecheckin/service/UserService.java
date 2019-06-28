package com.cop4331.group13.cavecheckin.service;

import com.cop4331.group13.cavecheckin.api.dto.user.UserAddRequestDto;
import com.cop4331.group13.cavecheckin.api.dto.user.UserResponseDto;
import com.cop4331.group13.cavecheckin.dao.TaCourseDao;
import com.cop4331.group13.cavecheckin.dao.UserDao;
import com.cop4331.group13.cavecheckin.domain.TaCourse;
import com.cop4331.group13.cavecheckin.domain.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao dao;

    @Autowired
    private TaCourseDao taCourseDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper mapper;

    private HashSet<String> kioskPins = null;

    public List<UserResponseDto> getUsersByRole(String role) {
        List<UserResponseDto> dtos = new ArrayList<>();
        dao.findByRole(role).forEach(user -> dtos.add(mapper.map(user, UserResponseDto.class)));

        return dtos;
    }

    public UserResponseDto addUser(UserAddRequestDto userDto, String role) {
        // Map the dto to a regular user object and generate a random kioskPin
        User user = mapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(role);
        user.setKioskPin(generatePin());
        user.setActive(true);

        // Save the user if the username is unique
        try {
            user = dao.save(user);
        } catch (DataIntegrityViolationException e) {
            return null;
        }

        // Parse the course list and save them to the TaCourse table
        if (userDto.getCourses() != null && !userDto.getCourses().isEmpty()) {
            String[] courses = userDto.getCourses().split(",");
            for (String courseId : courses) {
                taCourseDao.save(new TaCourse(0, user.getUserId(), Long.parseLong(courseId), true));
            }
        }

        // If the add was successful return a mapped response dto
        return mapper.map(user, UserResponseDto.class);
    }

    // Generate random pins until we find one that hasn't been used already
    private String generatePin() {
        // Pull the kiosk pins if we haven't already;
        if (kioskPins == null) {
            kioskPins = new HashSet<String>();
            kioskPins.addAll(dao.findAllKioskPins());
        }

        // Generate a unique pin
        String pin = DatatypeConverter.printLong((long)(Math.random() * 1000000));
        while (kioskPins.contains(pin)) {
            pin = DatatypeConverter.printLong((long)(Math.random() * 1000000));
        }

        // Add the pin to the set
        kioskPins.add(pin);

        return pin;
    }

    public UserResponseDto getUser(long userId) {
        User user = dao.findById(userId).orElse(null);
        if (user != null) {
            return mapper.map(user, UserResponseDto.class);
        } else {
            return null;
        }
    }

    public UserResponseDto deactivateUser(long userId) {
        User user = dao.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        user.setActive(false);
        user = dao.save(user);

        return mapper.map(user, UserResponseDto.class);
    }
}

package com.cop4331.group13.cavecheckin.service;

import com.cop4331.group13.cavecheckin.api.dto.user.*;
import com.cop4331.group13.cavecheckin.config.EncryptionUtil;
import com.cop4331.group13.cavecheckin.dao.CourseDao;
import com.cop4331.group13.cavecheckin.dao.TaCourseDao;
import com.cop4331.group13.cavecheckin.dao.UserDao;
import com.cop4331.group13.cavecheckin.domain.Course;
import com.cop4331.group13.cavecheckin.domain.TaCourse;
import com.cop4331.group13.cavecheckin.domain.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao dao;

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private TaCourseDao taCourseDao;

    @Autowired
    private CourseService courseService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper mapper;

    private HashSet<String> kioskPins = null;

    public UserResponseDto getSelf(String username) {
        User user = dao.findByUsername(username);
        return (user != null) ? mapper.map(user, UserResponseDto.class) : null;
    }

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

    public UserResponseDto updateUser(long userId, UserUpdateRequestDto userDto) {
        User user = dao.findById(userId).orElse(null);
        if (user == null) return null;

        // Update and save user object
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());

        user = dao.save(user);

        return mapper.map(user, UserResponseDto.class);
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

    public List<UserResponseDto> findTasByCourseId(long courseId, String username) throws AccessDeniedException {
        verifyCourseAccess(username, courseId);
        List<UserResponseDto> dtos = new ArrayList<>();
        dao.findUsersByCourseId(courseId).forEach(user -> dtos.add(mapper.map(user, UserResponseDto.class)));

        return dtos;
    }

    public UserResponseDto findTaByUserId(long userId, String username) throws AccessDeniedException {
        verifyTaAccess(username, userId);
        User ta = dao.findById(userId).orElse(null);
        if (ta == null) return null;

        TaResponseDto dto = mapper.map(ta, TaResponseDto.class);
        dto.setCourses(courseService.findCourseByTaId(userId));

        return dto;
    }

    public UserResponseDto updateTa(long userId, UserUpdateRequestDto userDto, String username) throws AccessDeniedException {
        verifyTaAccess(username, userId);
        User user = dao.findById(userId).orElse(null);
        if (user == null) return null;

        // Update and save user object
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());

        user = dao.save(user);

        // Update the courses
        List<TaCourse> taCourses = taCourseDao.findByUserId(userId);
        if ((taCourses != null && taCourses.size() > 0) || (userDto.getCourses() != null && !userDto.getCourses().isEmpty())) {
            // Map all of the user's existing courses to the courseId
            HashMap<Long, TaCourse> taCourseMap = new HashMap<>();
            if (taCourses != null) {
                for (TaCourse taCourse : taCourses) {
                    taCourseMap.put(taCourse.getCourseId(), taCourse);
                }
            }

            // Add any new courses to the table
            String[] courses = userDto.getCourses().split(",");
            for (String courseId : courses) {
                if (taCourseMap.containsKey(Long.valueOf(courseId))) {
                    TaCourse course = taCourseMap.get(Long.valueOf(courseId));
                    if (!course.isActive()) {
                        course.setActive(true);
                        taCourseDao.save(course);
                    }
                    taCourseMap.remove(Long.valueOf(courseId));
                } else {
                    taCourseDao.save(new TaCourse(0, userId, Long.valueOf(courseId), true));
                }
            }

            // Deactivate any previous courses that were not included in the new courses string
            for (TaCourse taCourse : taCourseMap.values()) {
                taCourse.setActive(false);
                taCourse = taCourseDao.save(taCourse);
            }
        }

        TaResponseDto responseDto = mapper.map(user, TaResponseDto.class);
        responseDto.setCourses(courseService.findCourseByTaId(userId));

        return responseDto;
    }

    public UserResponseDto deactivateTa(long userId, String username) throws AccessDeniedException {
        verifyTaAccess(username, userId);
        return deactivateUser(userId);
    }

    public UserResponseDto resetPassword(UserPasswordRequestDto dto, String username) {
        User user = dao.findByUsername(username);
        if (user == null) return null;

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user = dao.save(user);

        return mapper.map(user, UserResponseDto.class);
    }

    public UserResponseDto resetPin(String username) {
        User user = dao.findByUsername(username);
        if (user == null) return null;

        String oldPin = user.getKioskPin();
        user.setKioskPin(generatePin());

        user = dao.save(user);
        kioskPins.remove(oldPin);
        kioskPins.add(user.getKioskPin());

        return mapper.map(user, UserResponseDto.class);
    }

    public UserResponseDto updateSelf(UserUpdateRequestDto dto, String username) {
        Long userId = dao.findUserIdByUsername(username);
        return updateUser(userId, dto);
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

    public void verifyTaAccess(String username, long userId) throws AccessDeniedException {
        Long teacherId = dao.findUserIdByUsername(username);
        List<TaCourse> taCourses = taCourseDao.findByUserId(userId);
        if (teacherId == null || taCourses == null) throw new AccessDeniedException("You are not authorized to access this TA");
        for (TaCourse taCourse : taCourses) {
            Course course = courseDao.findById(taCourse.getCourseId()).orElse(null);
            if (course != null && teacherId == course.getUserId()) return;
        }

        throw new AccessDeniedException("You are not authorized to access this TA");
    }

    private void verifyCourseAccess(String username, long courseId) throws AccessDeniedException {
        Course course = courseDao.findById(courseId).orElse(null);
        Long userId = dao.findUserIdByUsername(username);
        if (userId == null || course == null || course.getUserId() != userId) throw new AccessDeniedException("You are not authorized to access this course");
    }

    public UserByPinResponseDto getUserByEncryptedPin(UserByPinRequestDto dto) {
        EncryptionUtil util = new EncryptionUtil();

        String pin = util.decrypt(dto.getEncryptedPin());

        User user = dao.findByKioskPin(pin);

        // Double check the results of the SQL query. It's wrong sometimes.
        if (user != null && user.getKioskPin() == pin)
            return mapper.map(user, UserByPinResponseDto.class);
        else
            return null;
    }
}

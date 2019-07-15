package com.cop4331.group13.cavecheckin.dao;

import com.cop4331.group13.cavecheckin.domain.Course;
import com.cop4331.group13.cavecheckin.domain.TaCourse;
import com.cop4331.group13.cavecheckin.domain.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

@Service
public class DbInit implements CommandLineRunner {
    private CourseDao courseDao;
    private UserDao userDao;
    private TaCourseDao taCourseDao;
    private PasswordEncoder passwordEncoder;

    public DbInit(CourseDao courseDao, UserDao userDao, TaCourseDao taCourseDao, PasswordEncoder passwordEncoder) {
        this.courseDao = courseDao;
        this.userDao = userDao;
        this.taCourseDao = taCourseDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // If an "admin" user does not exist, create it.
        if (userDao.findByUsername("admin") == null)
        {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setActive(true);

            userDao.save(admin);
        }

        populateTestData();
    }

    private void populateTestData() {
        String[] courseCodes = new String[]{
                "COP3223",
                "COP3502",
                "COP3503",
                "COT3100",
                "COP3330"
        };

        String[] teacherNames = new String[]{
                "Rick Deckard",
                "Sarah Connor",
                "Llewelyn Moss",
                "Clarice Starling",
        };

        String [] taNames = new String[] {
                "Pinky Penguin",
                "Sarah Lynn",
                "Herb Kazzaz",
                "Charlotte Moore",
                "Beatrice Horseman",
                "Butterscotch Horseman",
                "Lenny Turteltaub",
                "Sextina Aquafina",
                "Kelsey Jannings",
                "Vanessa Gekko",
                "Charley Witherspoon",
                "Vincent Adultman",
                "Corduroy Jackson-Jackson",
                "Rutabaga Rabbitowitz",
                "Hank Hippopopalous",
                "Ralph Stilton",
                "Stefani Stilton",
                "Woodchuck Coodchuck-Berkowitz",
                "Joseph Sugarman",
                "Honey Sugarman",
                "Courtney Portnoy",
                "Ana Spanikopita",
                "Flip McVicker",
                "Gina Cazador",
                "Pickles Aplenty",
        };

        List<User> teachers = createUsersWithRole(teacherNames, "TEACHER");
        List<User> tAs = createUsersWithRole(taNames, "TA");

        List<Course> courses = createCourses(courseCodes, teachers, tAs);
    }

    private ArrayList<Course> createCourses(String[] courseCodes, List<User> teachers, List<User> tAs) {
        Random rand = new Random();

        int year = 2019;

        ArrayList<Course> courses = new ArrayList<>();

        for (int i = 0; i < courseCodes.length; i++)
        {
            Course course = createTestCourse(courseCodes[i], year, "Spring", teachers.get(i % teachers.size()), tAs);
            courses.add(course);
            courseDao.save(course);

            if (rand.nextBoolean())
            {
                course = createTestCourse(courseCodes[i], year-1, "Fall", teachers.get(i % teachers.size()), tAs);
                courses.add(course);
                courseDao.save(course);
            }
        }

        return courses;
    }

    private Course createTestCourse(String courseCode, int year, String semester, User teacher, List<User> tAs) {
        Random rand = new Random();

        int taListSize = tAs.size();
        int courseTaCount = Math.min((rand.nextInt(10) + 1), taListSize);

        Course course = new Course();

        course.setUserId(teacher.getUserId());
        course.setCourseCode(courseCode);
        course.setSemester(semester);
        course.setYear(year);
        course.setActive(true);

        HashSet<Integer> courseTaIds = new HashSet<>();

        // Get a random collection of unique TA IDs.
        while (courseTaIds.size() < courseTaCount)
        {
            courseTaIds.add(rand.nextInt(taListSize));
        }

        for (Integer taId : courseTaIds)
        {
            TaCourse taCourse = new TaCourse();

            taCourse.setCourseId(course.getCourseId());
            taCourse.setUserId(taId);
            taCourse.setActive(true);

            taCourseDao.save(taCourse);
        }

        return course;
    }

    List<User> createUsersWithRole(String[] names, String role) {
        List<User> users = new ArrayList<>();

        for (String name : names)
        {
            User user = createTestUser(name);
            user.setRole(role);
            user.setActive(true);

            users.add(user);
            userDao.save(user);
        }

        return users;
    }

    private User createTestUser(String name)
    {
        User user = new User();

        // Split name string only at the first space
        String[] splitName = name.split(" ", 2);

        String firstName = splitName[0];
        String lastName = splitName[1];
        String username = String.format("%s%c", firstName.toLowerCase(), lastName.toLowerCase().charAt(0));
        String email = String.format("%s.%c@ucf.edu", firstName.toLowerCase(), lastName.toLowerCase().charAt(0));

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(firstName));
        user.setEmail(email);

        return user;
    }
}

package com.cop4331.group13.cavecheckin.dao;

import com.cop4331.group13.cavecheckin.domain.Course;
import com.cop4331.group13.cavecheckin.domain.Session;
import com.cop4331.group13.cavecheckin.domain.TaCourse;
import com.cop4331.group13.cavecheckin.domain.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.*;

@Service
public class DbInit implements CommandLineRunner {
    private CourseDao courseDao;
    private UserDao userDao;
    private TaCourseDao taCourseDao;
    private SessionDao sessionDao;
    private PasswordEncoder passwordEncoder;
    private HashSet<String> kioskPins;

    public DbInit(CourseDao courseDao, UserDao userDao, TaCourseDao taCourseDao, SessionDao sessionDao, PasswordEncoder passwordEncoder) {
        this.courseDao = courseDao;
        this.userDao = userDao;
        this.taCourseDao = taCourseDao;
        this.sessionDao = sessionDao;
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
            admin.setKioskPin("123");
            userDao.save(admin);
        }

        populateTestData();
    }

    private void populateTestData() {
        String[] courseCodesandNames = new String[]{
                "COP3223 Intro to C",
                "COP3502 Computer Science I",
                "COP3503 Computer Science II",
                "COT3100 Discrete Structures I",
                "COP3330 Object Oriented Programming"
        };

        String [][] teacherNamesAndPins = new String[][]{
                {"Rick Deckard", "206131"},
                {"Sarah Connor", "227038"},
                {"Llewelyn Moss", "881197"},
                {"Clarice Starling", "563327"}
        };

        String [][] taNamesAndPins = new String[][] {
                {"Pinky Penguin", "461396"},
                {"Sarah Lynn", "33232"},
                {"Herb Kazzaz", "566299"},
                {"Charlotte Moore", "516215"},
                {"Beatrice Horseman", "96168"},
                {"Butterscotch Horseman", "750065"},
                {"Lenny Turteltaub", "703802"},
                {"Sextina Aquafina", "383422"},
                {"Kelsey Jannings", "449557"},
                {"Vanessa Gekko", "744935"},
                {"Charley Witherspoon", "640327"},
                {"Vincent Adultman", "5266"},
                {"Corduroy Jackson-Jackson", "413211"},
                {"Rutabaga Rabbitowitz", "486449"},
                {"Hank Hippopopalous", "407139"},
                {"Ralph Stilton", "493076"},
                {"Stefani Stilton", "918923"},
                {"Woodchuck Coodchuck-Berkowitz", "725631"},
                {"Joseph Sugarman", "827401"},
                {"Honey Sugarman", "753201"},
                {"Courtney Portnoy", "619701"},
                {"Ana Spanikopita", "226399"},
                {"Flip McVicker", "411874"},
                {"Gina Cazador", "197875"},
                {"Pickles Aplenty", "532630"}
        };

        String [] studentNames = new String [] {
                "Tweek",
                "Bebe",
                "Bradley",
                "Clyde",
                "Craig",
                "Dougie",
                "Heidi",
                "Jimmy",
                "Timmy",
                "Token",
                "Randy",
                "Sharon",
                "Shelly",
                "Jimbo",
                "Gerald",
                "Sheila",
                "Ike",
                "Liane",
                "Stuart",
                "Carol",
                "Karen",
                "Kevin",
                "Stephen",
                "Linda",
                "Stan",
                "Kyle",
                "Eric",
                "Kenny",
                "Butters",
                "Wendy"
        };

        List<User> teachers = createUsersWithRole(teacherNamesAndPins, "TEACHER");
        List<User> tAs = createUsersWithRole(taNamesAndPins, "TA");

        List<Course> courses = createCourses(courseCodesandNames, teachers);

        HashMap<Course, User[]> taCourses = createTaCourseRecords(courses, tAs);

        List<Session> sessions = createSessions(studentNames, taCourses, courses);


    }

    private List<Session> createSessions(String [] studentNames, HashMap<Course, User[]> taCourses, List<Course> courses)
    {
        Random rand = new Random();

        int averageCurrentSessionCount = 5;
        int averageSessionCount = 20;
        int minimumSessionCount = 10;

        // Durations are given in seconds.
        int typicalAverageSessionDuration = 22 * 60;
        int minimumAverageSessionDuration = 9 * 60;

        int typicalAverageWaitTime = 10 * 60;
        int minimumAverageWaitTime = 5 * 60;

        HashMap<String, Month[]> semesterMonthRange = new HashMap<>();
        semesterMonthRange.put("Fall", new Month[] {Month.AUGUST, Month.NOVEMBER});
        semesterMonthRange.put("Spring", new Month[] {Month.JANUARY, Month.APRIL});
        semesterMonthRange.put("Summer", new Month[] {Month.MAY, Month.JULY});

        List<Session> sessions = new ArrayList<>();

        for (Course course : courses)
        {
            long courseId = course.getCourseId();
            User [] tAs = taCourses.get(course);

            int year = (int)course.getYear();
            Month firstMonth = semesterMonthRange.get(course.getSemester())[0];
            Month lastMonth = semesterMonthRange.get(course.getSemester())[1];

            int monthRange = lastMonth.getValue() - firstMonth.getValue();

            // Get a session count based on a Gaussian distribution and no less than a given number.
            int sessionCount = Math.max(
                    minimumSessionCount,
                    (int)(averageSessionCount * (1.0 + 0.5 * Math.abs(rand.nextGaussian())))
            );

            // Get a count of sessions to be currently open.
            int currentSessionCount = (int)(averageCurrentSessionCount * (1.0 + 0.5 * Math.abs(rand.nextGaussian())));

            // Get an average session duration based on a Gaussian distribution and no less than a given number.
            int averageSessionDuration = Math.max(
                    minimumAverageSessionDuration,
                    (int)(typicalAverageSessionDuration * (1.0 + 0.5 * Math.abs(rand.nextGaussian())/4))
            );

            // Get an average session wait time based on a Gaussian distribution and no less than a given number.
            int averageWaitTime = Math.max(
                    minimumAverageWaitTime,
                    (int)(typicalAverageWaitTime * (1.0 + 0.5 * Math.abs(rand.nextGaussian())/4))
            );

            int startingWaitTime = 3 * 60;
            int waitTime = startingWaitTime;

            int i = 0;

            if (course.isActive()) {
                for (; i < currentSessionCount; i++) {
                    waitTime += (int) (60 * (4.0 + 0.5 * Math.abs(rand.nextGaussian())));

                    // Start current sessions any time from 1 to 20 minutes ago
                    LocalDateTime startTime = LocalDateTime.now().minusSeconds(waitTime);

                    long taId = tAs[rand.nextInt(tAs.length)].getUserId();
                    String studentName = studentNames[rand.nextInt(studentNames.length)];

                    sessions.add(createTestCurrentSession(studentName, taId, courseId, startTime, startingWaitTime, waitTime));
                }
            }

            for (; i < sessionCount; i++)
            {
                Month month = firstMonth.plus(rand.nextInt(monthRange + 1));

                LocalDateTime startTime = LocalDateTime.of(
                        year,
                        month,
                        rand.nextInt(month.length(false)) + 1,
                        10 + rand.nextInt(7),  //
                        rand.nextInt(60)
                );

                // Lazy way to make sure we only get times that are prior to today.
                while (startTime.compareTo(LocalDateTime.now().minusDays(1)) >= 0)
                {
                    startTime = LocalDateTime.of(
                            year,
                            month,
                            rand.nextInt(month.length(false)) + 1,
                            10 + rand.nextInt(7),  //
                            rand.nextInt(60)
                    );
                }

                int day = startTime.getDayOfMonth();

                // If the day falls on a weekend, change it to a weekday.
                if (day > DayOfWeek.FRIDAY.getValue())
                {
                    if (day > 2)
                        startTime = startTime.minusDays(2);
                    else
                        startTime = startTime.plusDays(2);
                }

                LocalDateTime helpTime = startTime.plusSeconds((long)(averageWaitTime * (1 + 0.5 * Math.abs(rand.nextGaussian())/4)));
                LocalDateTime endTime = helpTime.plusSeconds((long)(averageSessionDuration * (1 + 0.5 * Math.abs(rand.nextGaussian())/4)));

                long taId = tAs[rand.nextInt(tAs.length)].getUserId();

                String studentName = studentNames[rand.nextInt(studentNames.length)];

                sessions.add(createTestSession(studentName, taId, courseId, startTime, helpTime, endTime));
            }
        }

        return sessions;
    }

    private Session createTestCurrentSession(String studentName, long taId, long courseId, LocalDateTime startTime, int startingWaitTime, int waitTime) {
        Random rand = new Random();

        Session session = new Session();
        session.setCourseId(courseId);
        session.setUserId(taId);
        session.setStudentName(studentName);
        session.setStartTime(convertDate(startTime));

        int helpTimeThreshold = startingWaitTime * 6;

        if (waitTime > helpTimeThreshold)
        {
            LocalDateTime helpTime = LocalDateTime.now().minusSeconds(rand.nextInt(helpTimeThreshold));
            session.setHelpTime(convertDate(helpTime));
        }

        sessionDao.save(session);

        return session;
    }

    private Session createTestSession(String studentName, Long taId, Long courseId, LocalDateTime startTime, LocalDateTime helpTime, LocalDateTime endTime)
    {
        Session session = new Session();
        session.setCourseId(courseId);
        session.setUserId(taId);
        session.setStudentName(studentName);
        session.setStartTime(convertDate(startTime));
        session.setHelpTime(convertDate(helpTime));
        session.setEndTime(convertDate(endTime));

        sessionDao.save(session);

        return session;
    }

    private Date convertDate(LocalDateTime dateTime)
    {
        return Date.from(dateTime.atZone( ZoneId.systemDefault()).toInstant());
    }

    private HashMap<Course, User[]> createTaCourseRecords(List<Course> courses, List<User> tAs)
    {

        Random rand = new Random();

        HashMap<Course, User[]> taCourses = new HashMap<>();

        int taListSize = tAs.size();

        for (Course course : courses)
        {
            int courseTaCount = Math.min((rand.nextInt(10) + 1), taListSize);

            HashSet<User> courseTas = new HashSet<>();

            // Get a random collection of unique TA IDs.
            while (courseTas.size() < courseTaCount)
            {
                courseTas.add(tAs.get(rand.nextInt(taListSize)));
            }

            for (User ta : courseTas)
            {
                TaCourse taCourse = new TaCourse();

                taCourse.setCourseId(course.getCourseId());
                taCourse.setUserId(ta.getUserId());
                taCourse.setActive(true);

                taCourseDao.save(taCourse);
            }

            taCourses.put(course, courseTas.toArray(new User[0]));
        }

        return taCourses;
    }

    private ArrayList<Course> createCourses(String[] courseCodesAndNames, List<User> teachers) {
        Random rand = new Random();

        int year = 2019;

        ArrayList<Course> courses = new ArrayList<>();

        for (int i = 0; i < courseCodesAndNames.length; i++)
        {
            String[] courseStringSplit = courseCodesAndNames[i].split(" ", 2);
            String courseCode = courseStringSplit[0];
            String courseName = courseStringSplit[1];
            User teacher = teachers.get(i % teachers.size());

            Course course = createTestCourse(courseCode, courseName, year, "Summer", teacher, true);
            courses.add(course);

            if (rand.nextBoolean())
            {
                course = createTestCourse(courseCode, courseName, year, "Spring", teacher, false);
                courses.add(course);
            }

            if (rand.nextBoolean())
            {
                course = createTestCourse(courseCode, courseName, year-1, "Fall", teacher, false);
                courses.add(course);
            }
        }

        return courses;
    }

    private Course createTestCourse(String courseCode, String courseName, int year, String semester, User teacher, boolean isActive) {
        Course course = new Course();

        course.setUserId(teacher.getUserId());
        course.setCourseCode(courseCode);
        course.setCourseName(courseName);
        course.setSemester(semester);
        course.setYear(year);
        course.setActive(isActive);

        courseDao.save(course);

        return course;
    }

    private List<User> createUsersWithRole(String[][] namesAndPins, String role) {
        List<User> users = new ArrayList<>();

        for (int i = 0; i < namesAndPins.length; i++)
        {
            User user = createTestUser(namesAndPins[i][0], namesAndPins[i][1]);
            user.setRole(role);
            user.setActive(true);

            users.add(user);
            userDao.save(user);
        }

        return users;
    }

    private User createTestUser(String name, String pin)
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
        user.setKioskPin(pin);

        return user;
    }

    private String generatePin() {
        // Pull the kiosk pins if we haven't already;
        if (kioskPins == null) {
            kioskPins = new HashSet<String>();
            kioskPins.addAll(userDao.findAllKioskPins());
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
}

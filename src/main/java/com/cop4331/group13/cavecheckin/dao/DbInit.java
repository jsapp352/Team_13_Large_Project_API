package com.cop4331.group13.cavecheckin.dao;

import com.cop4331.group13.cavecheckin.domain.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DbInit implements CommandLineRunner {
    private UserDao dao;
    private PasswordEncoder passwordEncoder;

    public DbInit(UserDao dao, PasswordEncoder passwordEncoder) {
        this.dao = dao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // If an "admin" user does not exist, create it.
        if (dao.findByUsername("admin") == null)
        {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setActive(true);

            dao.save(admin);
        }

        populateTestData();
    }

    private void populateTestData() {
        String[] courseNames = new String[]{
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
    }

    List<User> createUsersWithRole(String[] names, String role) {
        List<User> users = new ArrayList<>();

        for (String name : names)
        {
            User user = createTestUser(name);
            user.setRole(role);
            user.setActive(true);

            users.add(user);
            dao.save(user);
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

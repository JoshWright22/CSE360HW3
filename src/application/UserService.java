package application;

import java.util.ArrayList;
import java.util.List;

// simplified UserService stub
public class UserService {
    private List<User> users = new ArrayList<>();

    public User registerFirstUser(String username, String password) {
        User user = new User(username, password, "Admin");
        users.add(user);
        return user;
    }

    public User createUser(String username, String password) {
        User user = new User(username, password, "Student");
        users.add(user);
        return user;
    }

    public boolean login(String username, String password) {
        return users.stream().anyMatch(u -> u.getUserName().equals(username));
    }

    public User registerUsingInvite(String username, String password, String inviteCode) {
        return new User(username, password, "studentRole");
    }
}
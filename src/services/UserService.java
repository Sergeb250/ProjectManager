package services;

import models.User;

import java.util.ArrayList;
import java.util.List;

// Stores users in a list and tracks who is logged in.
public class UserService {

    private final List<User> users;
    private User currentUser;

    public UserService() {
        users = new ArrayList<>();
    }

    public boolean isFull() {
        return false;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public int getCount() {
        return users.size();
    }

    public User getAt(int index) {
        return users.get(index);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public boolean emailExists(String email) {
        return users.stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }
}

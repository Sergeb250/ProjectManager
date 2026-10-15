package services;

import models.User;

public class UserService {

    public static final int MAX_USERS = 10;

    private final User[] users;
    private int userCount;
    private User currentUser;

    public UserService() {
        users = new User[MAX_USERS];
        userCount = 0;
    }

    public boolean isFull() {
        return userCount >= MAX_USERS;
    }

    public void addUser(User user) {
        users[userCount] = user;
        userCount++;
    }

    public int getCount() {
        return userCount;
    }

    public User getAt(int index) {
        return users[index];
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public boolean emailExists(String email) {
        for (int i = 0; i < userCount; i++) {
            if (users[i].getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }
}

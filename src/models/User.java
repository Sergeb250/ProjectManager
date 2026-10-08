package models;

public abstract class User {

    private static int nextNumber = 1;

    private final String id;
    private final String name;
    private final String email;
    private final String role;

    public User(String name, String email, String role) {
        this.id = String.format("USR%03d", nextNumber);
        nextNumber++;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public abstract void printUserReport();

    public abstract boolean canUpdateAndDelete();

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}

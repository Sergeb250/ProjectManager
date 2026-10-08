package models;

public class AdminUser extends User {

    public AdminUser(String name, String email) {
        super(name, email, "Admin");
    }

    @Override
    public void printUserReport() {
        System.out.println(getId() + " | " + getName() + " | Admin | Can update and delete: Yes");
    }

    @Override
    public boolean canUpdateAndDelete() {
        return true;
    }
}

package models;

public class RegularUser extends User {

    public RegularUser(String name, String email) {
        super(name, email, "Regular");
    }

    @Override
    public void printUserReport() {
        System.out.println(getId() + " | " + getName() + " | Regular | Can update and delete: No");
    }

    @Override
    public boolean canUpdateAndDelete() {
        return false;
    }
}

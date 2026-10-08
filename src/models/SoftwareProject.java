package models;

public class SoftwareProject extends Project {

    private final String technology;

    public SoftwareProject(String name, String description, int teamSize, int duration,
                           double budget, String leadName, String technology) {
        super(name, description, teamSize, duration, budget, leadName);
        this.technology = technology;
    }

    @Override
    public String getType() {
        return "Software";
    }

    @Override
    public String getProjectDetails() {
        return "Technology: " + technology;
    }

    public String getTechnology() {
        return technology;
    }
}

package models;

public class SoftwareProject extends Project {

    private final String technology;

    public SoftwareProject(String name, String description, int teamSize, int duration,
                           double budget, String leadName, String technology) {
        super(name, description, teamSize, duration, budget, leadName);
        this.technology = technology;
    }

    // Used when this project is read back from the data file.
    public SoftwareProject(String id, String name, String description, int teamSize, int duration,
                           double budget, String leadName, String technology) {
        super(id, name, description, teamSize, duration, budget, leadName);
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

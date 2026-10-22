package models;

public class HardwareProject extends Project {

    private final String equipment;

    public HardwareProject(String name, String description, int teamSize, int duration,
                           double budget, String leadName, String equipment) {
        super(name, description, teamSize, duration, budget, leadName);
        this.equipment = equipment;
    }

    // Used when this project is read back from the data file.
    public HardwareProject(String id, String name, String description, int teamSize, int duration,
                           double budget, String leadName, String equipment) {
        super(id, name, description, teamSize, duration, budget, leadName);
        this.equipment = equipment;
    }

    @Override
    public String getType() {
        return "Hardware";
    }

    @Override
    public String getProjectDetails() {
        return "Equipment: " + equipment;
    }

    public String getEquipment() {
        return equipment;
    }
}

package models;

public class HardwareProject extends Project {

    private final String equipment;

    public HardwareProject(String name, String description, int teamSize, int duration,
                           double budget, String leadName, String equipment) {
        super(name, description, teamSize, duration, budget, leadName);
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

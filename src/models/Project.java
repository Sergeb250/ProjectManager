package models;

import interfaces.Completable;

import java.util.ArrayList;
import java.util.List;

// Base class for all projects. Tasks live in an ArrayList, so the list can grow.
public abstract class Project implements Completable {

    private static int nextNumber = 1;

    private final String id;
    private final String name;
    private final String description;
    private final int teamSize;
    private final int duration;
    private final double budget;
    private final String leadName;
    private final List<Task> tasks;

    // Creates a project and assigns the next PRJ ID.
    public Project(String name, String description, int teamSize, int duration, double budget, String leadName) {
        this(nextProjectId(), name, description, teamSize, duration, budget, leadName);
    }

    // Used when a project is loaded from the file and already has an ID.
    public Project(String id, String name, String description, int teamSize, int duration,
                   double budget, String leadName) {
        this.id = id;
        rememberNumber(id);
        this.name = name;
        this.description = description;
        this.teamSize = teamSize;
        this.duration = duration;
        this.budget = budget;
        this.leadName = leadName;
        this.tasks = new ArrayList<>();
    }

    public abstract String getType();

    public abstract String getProjectDetails();

    // Empty projects are not completed. Every task must be completed.
    @Override
    public boolean isCompleted() {
        if (tasks.isEmpty()) {
            return false;
        }
        return tasks.stream().allMatch(Task::isCompleted);
    }

    // Completed tasks divided by all tasks, as a percent. Zero when there are no tasks.
    public double getCompletionRate() {
        if (tasks.isEmpty()) {
            return 0.0;
        }
        long completed = tasks.stream().filter(Task::isCompleted).count();
        return (completed * 100.0) / tasks.size();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public int getDuration() {
        return duration;
    }

    public double getBudget() {
        return budget;
    }

    public String getLeadName() {
        return leadName;
    }

    public int getTaskCount() {
        return tasks.size();
    }

    private static String nextProjectId() {
        String id = String.format("PRJ%03d", nextNumber);
        nextNumber++;
        return id;
    }

    private static void rememberNumber(String id) {
        String digits = id.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return;
        }
        int number = Integer.parseInt(digits);
        if (number >= nextNumber) {
            nextNumber = number + 1;
        }
    }
}

package models;

import interfaces.Completable;

public class Task implements Completable {

    private static int nextNumber = 1;

    private final String id;
    private final String name;
    private final String assignedTo;
    private final int hours;
    private TaskStatus status;

    public Task(String name, String assignedTo, TaskStatus status, int hours) {
        this.id = String.format("TSK%03d", nextNumber);
        nextNumber++;
        this.name = name;
        this.assignedTo = assignedTo;
        this.status = status;
        this.hours = hours;
    }

    @Override
    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED;
    }

    public void printDetails() {
        System.out.println("Task ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Assigned To: " + assignedTo);
        System.out.println("Status: " + status.getLabel());
        System.out.println("Hours: " + hours);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public int getHours() {
        return hours;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}

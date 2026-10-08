package models;

import java.util.Locale;

public enum TaskStatus {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static TaskStatus fromText(String text) {
        if (text == null) {
            return null;
        }
        String cleaned = text.trim().toLowerCase(Locale.US);
        if (cleaned.equals("1") || cleaned.equals("pending")) {
            return PENDING;
        }
        if (cleaned.equals("2") || cleaned.equals("in progress")
                || cleaned.equals("inprogress") || cleaned.equals("in-progress")) {
            return IN_PROGRESS;
        }
        if (cleaned.equals("3") || cleaned.equals("completed")) {
            return COMPLETED;
        }
        return null;
    }
}

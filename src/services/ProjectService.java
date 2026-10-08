package services;

import models.Project;

import java.util.Locale;

public class ProjectService {

    private static final int MAX_PROJECTS = 20;

    private final Project[] projects;
    private int projectCount;

    public ProjectService() {
        projects = new Project[MAX_PROJECTS];
        projectCount = 0;
    }

    public boolean isFull() {
        return projectCount >= MAX_PROJECTS;
    }

    public void add(Project project) {
        projects[projectCount] = project;
        projectCount++;
    }

    public int getCount() {
        return projectCount;
    }

    public Project getAt(int index) {
        return projects[index];
    }

    public Project findById(String projectId) {
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getId().equalsIgnoreCase(projectId)) {
                return projects[i];
            }
        }
        return null;
    }

    public void printAll() {
        Project[] copy = new Project[projectCount];
        for (int i = 0; i < projectCount; i++) {
            copy[i] = projects[i];
        }
        sortByName(copy);
        printTable(copy);
        System.out.println("Total projects: " + projectCount);
    }

    public void printByType(String type) {
        Project[] matches = collectByType(type);
        if (matches.length == 0) {
            System.out.println("None.");
            return;
        }
        sortByName(matches);
        printTable(matches);
    }

    public void printByBudget(double min, double max) {
        Project[] matches = collectByBudget(min, max);
        if (matches.length == 0) {
            System.out.println("No projects found.");
            return;
        }
        sortByName(matches);
        System.out.printf(Locale.US, "Projects with budget from $%,.2f to $%,.2f:%n", min, max);
        printTable(matches);
    }

    private void printTable(Project[] matches) {
        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────");
        System.out.printf("%-8s | %-20s | %-10s | %-9s | %s%n",
                "ID", "PROJECT NAME", "TYPE", "TEAM SIZE", "BUDGET");
        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────");
        for (int i = 0; i < matches.length; i++) {
            matches[i].displayCatalogRow();
        }
    }

    private Project[] collectByType(String type) {
        int matches = 0;
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getType().equals(type)) {
                matches++;
            }
        }
        return copyMatches(type, matches);
    }

    private Project[] copyMatches(String type, int matches) {
        Project[] result = new Project[matches];
        int index = 0;
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getType().equals(type)) {
                result[index] = projects[i];
                index++;
            }
        }
        return result;
    }

    private Project[] collectByBudget(double min, double max) {
        int matches = 0;
        for (int i = 0; i < projectCount; i++) {
            if (inBudget(projects[i], min, max)) {
                matches++;
            }
        }

        Project[] result = new Project[matches];
        int index = 0;
        for (int i = 0; i < projectCount; i++) {
            if (inBudget(projects[i], min, max)) {
                result[index] = projects[i];
                index++;
            }
        }
        return result;
    }

    private boolean inBudget(Project project, double min, double max) {
        return project.getBudget() >= min && project.getBudget() <= max;
    }

    private void sortByName(Project[] list) {
        for (int pass = 0; pass < list.length - 1; pass++) {
            for (int i = 0; i < list.length - 1 - pass; i++) {
                String left = list[i].getName();
                String right = list[i + 1].getName();
                if (left.compareToIgnoreCase(right) > 0) {
                    Project temp = list[i];
                    list[i] = list[i + 1];
                    list[i + 1] = temp;
                }
            }
        }
    }
}

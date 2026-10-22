import services.ProjectService;
import services.ReportService;
import services.TaskService;
import services.UserService;
import utils.ConsoleMenu;
import utils.FileUtils;
import utils.SampleDataLoader;

import java.io.IOException;
import java.util.NoSuchElementException;

// Application entry point for the Task Manager console app.
public class Main {

    // Loads saved projects, opens the menu, then writes the file again on exit.
    public static void main(String[] args) {
        ProjectService projectService = new ProjectService();
        TaskService taskService = new TaskService(projectService);
        ReportService reportService = new ReportService(taskService);
        UserService userService = new UserService();
        SampleDataLoader.loadUsers(userService);

        System.out.println("Loading projects from file...");
        boolean loaded = FileUtils.loadProjects(FileUtils.DATA_FILE, projectService);
        if (loaded) {
            System.out.println("✓ " + projectService.getCount()
                    + " projects loaded successfully from projects_data.json");
        } else {
            System.out.println("→ Starting with empty catalog.");
        }

        try {
            ConsoleMenu menu = new ConsoleMenu(projectService, taskService, reportService, userService);
            menu.start();
        } catch (NoSuchElementException ex) {
            System.out.println();
            System.out.println("Goodbye.");
        } finally {
            try {
                System.out.println("Saving project data...");
                FileUtils.saveProjects(FileUtils.DATA_FILE, projectService);
                System.out.println("Data written to projects_data.json successfully!");
            } catch (IOException ex) {
                System.out.println("✗ Error: Could not save projects_data.json");
            }
        }
    }
}

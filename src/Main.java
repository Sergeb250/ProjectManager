import services.ProjectService;
import services.ReportService;
import services.TaskService;
import services.UserService;
import utils.ConsoleMenu;
import utils.SampleDataLoader;

import java.util.NoSuchElementException;

public class Main {

    public static void main(String[] args) {
        ProjectService projectService = new ProjectService();
        TaskService taskService = new TaskService(projectService);
        ReportService reportService = new ReportService();
        UserService userService = new UserService();
        SampleDataLoader.load(projectService, taskService, userService);

        try {
            ConsoleMenu menu = new ConsoleMenu(projectService, taskService, reportService, userService);
            menu.start();
        } catch (NoSuchElementException ex) {
            System.out.println();
            System.out.println("Goodbye.");
        }
    }
}

package utils;

import models.AdminUser;
import models.HardwareProject;
import models.RegularUser;
import models.SoftwareProject;
import models.TaskStatus;
import services.ProjectService;
import services.TaskService;
import services.UserService;

public final class SampleDataLoader {

    private SampleDataLoader() {
    }

    public static void load(ProjectService projectService, TaskService taskService, UserService userService) {
        userService.addUser(new AdminUser("eric mugisha", "eric.mugisha@amalitech.com"));
        userService.addUser(new RegularUser("shima elyse", "shima.elsyse@gmail.com"));
        userService.setCurrentUser(userService.getAt(0));

        SoftwareProject alpha = new SoftwareProject(
                "Alpha Tracker", "Task tracking app for startups", 5, 90, 15000, "Alice Johnson", "Java");
        HardwareProject iot = new HardwareProject(
                "IoT Sensor Kit", "Sensor prototype for smart devices", 3, 60, 10000, "Bob Smith", "Sensors");
        SoftwareProject mobile = new SoftwareProject(
                "Mobile Checkout", "Phone checkout for retail stores", 4, 40, 22000, "Alice Johnson", "Kotlin");
        HardwareProject robot = new HardwareProject(
                "Warehouse Robot", "Moving shelf robot for a warehouse", 6, 120, 48000, "Alice Johnson", "Motors");
        SoftwareProject payroll = new SoftwareProject(
                "Payroll Portal", "Staff pay and leave portal", 2, 30, 8000, "Bob Smith", "Java");

        projectService.addProject(alpha);
        projectService.addProject(iot);
        projectService.addProject(mobile);
        projectService.addProject(robot);
        projectService.addProject(payroll);

        taskService.addTask(alpha, "Design Database", "Alice Johnson", TaskStatus.COMPLETED, 8);
        taskService.addTask(alpha, "Implement API", "Bob Smith", TaskStatus.IN_PROGRESS, 12);
        taskService.addTask(alpha, "Write Unit Tests", "Bob Smith", TaskStatus.PENDING, 6);
        taskService.addTask(iot, "Assemble Board", "Bob Smith", TaskStatus.COMPLETED, 10);
        taskService.addTask(iot, "Calibrate Sensor", "Alice Johnson", TaskStatus.PENDING, 4);
        taskService.addTask(mobile, "Build Cart", "Alice Johnson", TaskStatus.COMPLETED, 5);
        taskService.addTask(mobile, "Payment Flow", "Bob Smith", TaskStatus.COMPLETED, 7);
        taskService.addTask(robot, "Frame Build", "Bob Smith", TaskStatus.PENDING, 20);
    }
}

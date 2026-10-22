package utils;

import services.ProjectService;
import services.ReportService;
import services.UserService;

import java.util.Scanner;

// Report screen: prints the completion table, then waits to go back.
class ReportMenu extends MenuScreen {

    private final ProjectService projectService;
    private final ReportService reportService;

    ReportMenu(Scanner scanner, ProjectService projectService, ReportService reportService, UserService userService) {
        super(scanner, userService);
        this.projectService = projectService;
        this.reportService = reportService;
    }

    void open() {
        printBanner("PROJECT STATUS REPORT");
        System.out.println();
        reportService.printReport(projectService);
        System.out.println();
        System.out.println("1. <<Back");
        readChoice("Enter your choice: ", 1, 1);
    }
}

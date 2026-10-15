package services;

import models.Project;
import models.StatusReport;

import java.util.Locale;

public class ReportService {

    public void printReport(ProjectService projectService) {
        int count = projectService.getCount();
        if (count == 0) {
            System.out.println("No projects found.");
            return;
        }

        double[] percentages = new double[count];

        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────");
        System.out.printf("%-10s | %-20s | %5s | %9s | %s%n",
                "PROJECT ID", "PROJECT NAME", "TASKS", "COMPLETED", "PROGRESS (%)");
        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────");

        for (int i = 0; i < count; i++) {
            StatusReport report = new StatusReport(projectService.getAt(i));
            report.printRow();
            percentages[i] = report.getCompletionPercent();
        }

        double average = averageOf(percentages);

        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────");
        System.out.printf(Locale.US, "AVERAGE COMPLETION: %.2f%%%n", average);
        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────");
    }

    public void printReport(Project project) {
        StatusReport report = new StatusReport(project);
        report.print();
    }

    public double calculateAverageCompletion(ProjectService projectService) {
        int count = projectService.getCount();
        if (count == 0) {
            return 0.0;
        }
        double[] percentages = new double[count];
        for (int i = 0; i < count; i++) {
            percentages[i] = projectService.getAt(i).calculateCompletionPercentage();
        }
        return averageOf(percentages);
    }

    private double averageOf(double[] percentages) {
        double sum = 0;
        for (int i = 0; i < percentages.length; i++) {
            sum += percentages[i];
        }
        return sum / percentages.length;
    }
}

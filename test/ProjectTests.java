import models.SoftwareProject;
import models.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.ProjectService;
import services.TaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectTests {

    private SoftwareProject project;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        project = new SoftwareProject("Alpha", "Demo", 3, 10, 1000, "Tuyizere", "Java");
        taskService = new TaskService(new ProjectService());
    }

    @Test
    void noTasksIsZero() {
        assertEquals(0.0, taskService.calculateCompletionPercentage(project), 0.001);
    }

    @Test
    void oneCompletedOutOfTwoIs50() {
        taskService.addTask(project, "Design", "", TaskStatus.COMPLETED, 2);
        taskService.addTask(project, "Build", "", TaskStatus.PENDING, 3);
        assertEquals(50.0, taskService.calculateCompletionPercentage(project), 0.001);
    }

    @Test
    void allCompletedIs100() {
        taskService.addTask(project, "Design", "", TaskStatus.COMPLETED, 2);
        taskService.addTask(project, "Build", "", TaskStatus.COMPLETED, 3);
        assertEquals(100.0, taskService.calculateCompletionPercentage(project), 0.001);
    }
}

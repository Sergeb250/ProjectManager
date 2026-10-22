import models.AdminUser;
import models.RegularUser;
import models.SoftwareProject;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import services.ProjectService;
import services.TaskService;
import utils.exceptions.TaskNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Project creation, tasks, completion, and admin delete access")
@ExtendWith(MockitoExtension.class)
public class ProjectTests {

    @Mock
    private ProjectService projectService;

    private TaskService taskService;
    private SoftwareProject project;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(projectService);
        project = new SoftwareProject("Alpha", "Demo", 3, 10, 1000, "Tuyizere", "Java");
    }

    @Test
    void createProject() {
        ProjectService realProjectService = new ProjectService();
        realProjectService.addProject(project);

        assertEquals("Alpha", project.getName());
        assertEquals("Software", project.getType());
        assertEquals(1, realProjectService.getCount());
    }

    @Test
    void addTask() {
        Task task = taskService.addTask(project, "Design", "", TaskStatus.PENDING, 2);

        assertEquals("Design", task.getName());
        assertEquals(1, project.getTaskCount());
    }

    @Test
    void calculateCompletion() {
        taskService.addTask(project, "Design", "", TaskStatus.COMPLETED, 2);
        taskService.addTask(project, "Build", "", TaskStatus.PENDING, 3);

        double percentage = taskService.calculateCompletionPercentage(project);

        assertEquals(50.0, percentage);
    }

    @Test
    void missingTaskThrows() {
        when(projectService.getCount()).thenReturn(0);

        assertThrows(TaskNotFoundException.class, () -> taskService.findRequired("TSK999"));
        verify(projectService).getCount();
    }

    @Test
    void adminCanDeleteProject() {
        AdminUser admin = new AdminUser("Eric", "eric@amalitech.com");

        assertTrue(admin.canUpdateAndDelete());
    }

    @Test
    void regularUserCannotDeleteProject() {
        RegularUser user = new RegularUser("Tuyizere", "tuyizere@amalitech.com");

        assertFalse(user.canUpdateAndDelete());
    }
}

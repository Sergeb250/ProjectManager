import models.SoftwareProject;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import services.ProjectService;
import services.TaskService;
import utils.exceptions.TaskNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskTests {

    @Mock
    private ProjectService projectService;

    private Task task;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        SoftwareProject project = new SoftwareProject("Alpha", "Demo", 3, 10, 1000, "Tuyizere", "Java");
        taskService = new TaskService(projectService);
        task = spy(taskService.addTask(project, "Design", "", TaskStatus.PENDING, 2));
    }

    @Test
    void updateStatusChangesPendingToCompleted() {
        taskService.updateStatus(task, TaskStatus.COMPLETED);
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        verify(task).setStatus(TaskStatus.COMPLETED);
    }

    @Test
    void missingIdThrows() {
        when(projectService.getCount()).thenReturn(0);
        assertThrows(TaskNotFoundException.class, () -> taskService.findRequired("TSK999"));
        verify(projectService).getCount();
    }
}

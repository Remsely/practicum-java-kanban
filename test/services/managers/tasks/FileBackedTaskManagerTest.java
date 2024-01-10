package services.managers.tasks;

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.enums.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest {
    private static final String FILE_PATH = "test/backup/csv/test_manager.csv";

    @BeforeEach
    public void initManager() {
        manager = getManager();
    }

    @AfterEach
    public void clearManager() {
        manager.clear();
    }

    protected TaskManager getManager() {
        return new FileBackedTaskManager(FILE_PATH);
    }

    @Test
    public void shouldRepairEmptyManager() {
        assertEquals(0, manager.getTasks().size(), "Список задач не пуст.");
        assertEquals(0, manager.getSubtasks().size(), "Список подзадач не пуст.");
        assertEquals(0, manager.getEpics().size(), "Список эпиков не пуст.");
        assertEquals(0, manager.getHistory().size(), "Список истории не пуст.");
    }

    @Test
    public void shouldRepairEpicWithoutSubtasks() {
        Epic epic = new Epic("Epic 1", "Description");
        int epicId = manager.add(epic);

        initManager();

        assertEquals(epic, manager.getEpic(epicId), "Эпик не добавлен.");
        assertEquals(1, manager.getEpics().size(), "Неверное количество эпиков.");
        assertEquals(epic, manager.getEpic(epicId), "Эпики не совпадают.");
        assertEquals(0, manager.getSubtasks().size(), "Список подзадач не пуст.");
    }

    @Test
    public void shouldRepairFulledManagerCorrectly() {
        Epic epic1 = new Epic("Epic 1", "Description");
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW);

        int epic1Id = manager.add(epic1);
        int task1Id = manager.add(task2);
        int task2Id = manager.add(task1);

        Subtask subtask1 = new Subtask(epic1Id, "Subtask1", "Description", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(epic1Id, "Subtask2", "Description", TaskStatus.NEW);

        int subtask1Id = manager.add(subtask1);
        int subtask2Id = manager.add(subtask2);

        manager.getTask(task1Id);
        manager.getTask(task2Id);
        manager.getEpic(epic1Id);
        manager.getSubtask(subtask1Id);
        manager.getSubtask(subtask2Id);

        List<Task> history = manager.getHistory();
        List<Task> tasks = manager.getTasks();
        List<Subtask> subtasks = manager.getSubtasks();
        List<Epic> epics = manager.getEpics();

        initManager();

        assertEquals(tasks, manager.getTasks(), "Задачи не совпадают.");
        assertEquals(subtasks, manager.getSubtasks(), "Подзадачи не совпадают.");
        assertEquals(epics, manager.getEpics(), "Эпики не совпадают.");
        assertEquals(history, manager.getHistory(), "Истории не совпадают.");
    }
}
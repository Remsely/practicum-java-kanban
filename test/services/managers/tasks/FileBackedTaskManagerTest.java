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

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private static final String FILE_PATH = "test/backup/csv/test_manager.csv";

    @BeforeEach
    public void initManager() {
        manager = new FileBackedTaskManager(FILE_PATH);
    }

    @AfterEach
    public void clearManager() {
        manager.removeAllTasks();
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
        int epicId = manager.createTask(epic);

        manager = new FileBackedTaskManager(FILE_PATH);

        assertEquals(epic, manager.getEpicByID(epicId), "Эпик не добавлен.");
        assertEquals(1, manager.getEpics().size(), "Неверное количество эпиков.");
        assertEquals(epic, manager.getEpicByID(epicId), "Эпики не совпадают.");
        assertEquals(0, manager.getSubtasks().size(), "Список подзадач не пуст.");
    }

    @Test
    public void shouldRepairFulledManagerCorrectly() {
        Epic epic1 = new Epic("Epic 1", "Description");
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW);

        int epic1Id = manager.createTask(epic1);
        int task1Id = manager.createTask(task2);
        int task2Id = manager.createTask(task1);

        Subtask subtask1 = new Subtask(epic1Id, "Subtask1", "Description", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(epic1Id, "Subtask2", "Description", TaskStatus.NEW);

        int subtask1Id = manager.createTask(subtask1);
        int subtask2Id = manager.createTask(subtask2);

        manager.getTaskByID(task1Id);
        manager.getTaskByID(task2Id);
        manager.getEpicByID(epic1Id);
        manager.getSubtaskByID(subtask1Id);
        manager.getSubtaskByID(subtask2Id);

        List<Task> history = manager.getHistory();
        List<Task> tasks = manager.getTasks();
        List<Subtask> subtasks = manager.getSubtasks();
        List<Epic> epics = manager.getEpics();

        manager = new FileBackedTaskManager(FILE_PATH);

        assertEquals(tasks, manager.getTasks(), "Задачи не совпадают.");
        assertEquals(subtasks, manager.getSubtasks(), "Подзадачи не совпадают.");
        assertEquals(epics, manager.getEpics(), "Эпики не совпадают.");
        assertEquals(history, manager.getHistory(), "Истории не совпадают.");
    }
}
package services.managers.tasks;

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.enums.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    @Test
    void shouldReturnCorrectListWhenTasksAreExist() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);

        manager.createTask(task);

        final List<Task> gottenTasks = manager.getTasks();

        assertNotNull(gottenTasks, "Задачи не возвращаются.");
        assertEquals(1, gottenTasks.size(), "Неправильная длинна списка задач.");
        assertEquals(task, gottenTasks.get(0), "Задачи в списке не совпадают!");
    }

    @Test
    void shouldReturnEmptyListWhenTasksAreNotExist() {
        final List<Task> gottenTasks = manager.getTasks();

        assertNotNull(gottenTasks, "Задачи не возвращаются.");
        assertEquals(0, gottenTasks.size(), "Неправильная длинна списка задач.");
    }

    @Test
    void shouldReturnCorrectListWhenEpicsAreExist() {
        Epic epic = new Epic("Epic 1", "Description 1");

        manager.createTask(epic);

        final List<Epic> gottenTasks = manager.getEpics();

        assertNotNull(gottenTasks, "Эпики не возвращаются.");
        assertEquals(1, gottenTasks.size(), "Неправильная длинна списка эпиков.");
        assertEquals(epic, gottenTasks.get(0), "Эпики в списке не совпадают!");
    }

    @Test
    void shouldReturnEmptyListWhenEpicsAreNotExist() {
        final List<Epic> gottenTasks = manager.getEpics();

        assertNotNull(gottenTasks, "Эпики не возвращаются.");
        assertEquals(0, gottenTasks.size(), "Неправильная длинна списка эпиков.");
    }

    @Test
    void shouldReturnCorrectListWhenSubtasksAreExist() {
        Epic epic = new Epic("Epic 1", "Description 1");
        final int epicId = manager.createTask(epic);

        Subtask subtask = new Subtask(epicId, "Subtask 1", "Description 1", TaskStatus.NEW);
        manager.createTask(subtask);

        final List<Subtask> gottenTasks = manager.getSubtasks();

        assertNotNull(gottenTasks, "Подзадачи не возвращаются.");
        assertEquals(1, gottenTasks.size(), "Неправильная длинна списка подзадач.");
        assertEquals(subtask, gottenTasks.get(0), "Подзадачи в списке не совпадают!");
    }

    @Test
    void shouldReturnEmptyListWhenSubtasksAreNotExist() {
        final List<Subtask> gottenTasks = manager.getSubtasks();

        assertNotNull(gottenTasks, "Подзадачи не возвращаются.");
        assertEquals(0, gottenTasks.size(), "Неправильная длинна списка подзадач.");
    }

    @Test
    void shouldRemoveAllTasksSubtasksEndEpics() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Epic epic = new Epic("Epic 1", "Description 1");
        final int epicId = manager.createTask(epic);
        manager.createTask(task);

        Subtask subtask = new Subtask(epicId, "Subtask 1", "Description 1", TaskStatus.NEW);
        manager.createTask(subtask);

        manager.removeAllTasks();

        final List<Task> gottenTasks = manager.getTasks();
        final List<Subtask> gottenSubtasks = manager.getSubtasks();
        final List<Epic> gottenEpics = manager.getEpics();

        assertNotNull(gottenTasks, "Задачи не возвращаются.");
        assertNotNull(gottenSubtasks, "Подзадачи не возвращаются.");
        assertNotNull(gottenEpics, "Эпики не возвращаются.");

        assertEquals(0, gottenTasks.size(), "Неправильная длинна списка задач.");
        assertEquals(0, gottenSubtasks.size(), "Неправильная длинна списка подзадач.");
        assertEquals(0, gottenEpics.size(), "Неправильная длинна списка эпиков.");
    }

    @Test
    void shouldReturnTaskWhenItIsExist() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        final int taskId = manager.createTask(task);

        final Task gottenTask = manager.getTaskByID(taskId);

        assertNotNull(gottenTask, "Задача не возвращается.");
        assertEquals(task, gottenTask, "Задачи не совпадают.");
    }

    @Test
    void shouldReturnNullWhenTaskIsNotExist() {
        final Task gottenTask = manager.getTaskByID(1);

        assertNull(gottenTask, "Задача возвращается.");
    }

    @Test
    void shouldReturnEpicWhenItIsExist() {
        Epic epic = new Epic("Task 1", "Description 1");
        final int id = manager.createTask(epic);

        final Epic gottenEpic = manager.getEpicByID(id);

        assertNotNull(gottenEpic, "Эпик не возвращается.");
        assertEquals(epic, gottenEpic, "Эпики не совпадают.");
    }

    @Test
    void shouldReturnNullWhenEpicIsNotExist() {
        final Epic gottedEpic = manager.getEpicByID(1);

        assertNull(gottedEpic, "Эпик возвращается.");
    }

    @Test
    void shouldReturnSubtaskWhenItIsExist() {
        Epic epic = new Epic("Task 1", "Description 1");
        final int epicID = manager.createTask(epic);

        Subtask subtask = new Subtask(epicID, "Subtask 1", "Description 1", TaskStatus.NEW);
        final int subtaskID = manager.createTask(subtask);

        final Subtask gottenSubtask = manager.getSubtaskByID(subtaskID);

        assertNotNull(gottenSubtask, "Подзадача не возвращается.");
        assertEquals(subtask, gottenSubtask, "Подзадачи не совпадают.");
    }

    @Test
    void shouldNotCreateSubtaskIfEpicIsNotExist() {
        Subtask subtask = new Subtask(1, "Subtask 1", "Description 1", TaskStatus.NEW);

        assertThrows(NullPointerException.class, () -> manager.createTask(subtask));
    }

    @Test
    void shouldReturnNullWhenSubtaskIsNotExist() {
        Subtask gottenSubtask = manager.getSubtaskByID(1);

        assertNull(gottenSubtask, "Подзадача возвращается.");
    }

    @Test
    void shouldUpdateTaskAndReturnTrueIfItIsExist() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        final int taskId = manager.createTask(task);

        Task newTask = new Task("Task 1", "Description 1", TaskStatus.IN_PROGRESS);
        boolean isUpdated = manager.updateTask(taskId, newTask);

        assertTrue(isUpdated, "Задача не обновлена.");
        assertEquals(newTask, manager.getTaskByID(taskId), "Задачи не совпадают.");
    }

    @Test
    void shouldReturnFalseOnUpdateIfTaskIsNotExist() {
        Task newTask = new Task("Task 1", "Description 1", TaskStatus.IN_PROGRESS);
        boolean isUpdated = manager.updateTask(1, newTask);

        assertFalse(isUpdated, "Задача обновлена.");
    }

    @Test
    void shouldUpdateSubtaskAndReturnTrueIfItIsExist() {
        Epic epic = new Epic("Task 1", "Description 1");
        final int epicID = manager.createTask(epic);

        Subtask subtask = new Subtask(epicID, "Subtask 1", "Description 1", TaskStatus.NEW);
        final int subtaskID = manager.createTask(subtask);

        Subtask newSubtask = new Subtask(epicID, "Subtask 1", "Description 1", TaskStatus.IN_PROGRESS);
        boolean isUpdated = manager.updateTask(subtaskID, newSubtask);

        assertTrue(isUpdated, "Подзадача не обновлена.");
        assertEquals(newSubtask, manager.getSubtaskByID(subtaskID), "Подзадачи не совпадают.");
    }

    @Test
    void shouldReturnFalseOnUpdateIfSubtaskIsNotExist() {
        Epic epic = new Epic("Task 1", "Description 1");
        final int epicID = manager.createTask(epic);

        Subtask newTask = new Subtask(epicID, "Subtask 1", "Description 1", TaskStatus.IN_PROGRESS);
        boolean isUpdated = manager.updateTask(1, newTask);

        assertFalse(isUpdated, "Подзадача обновлена.");
    }

    @Test
    void shouldRemoveTaskAndReturnTrueIfItIsExist() {
        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        final int taskId = manager.createTask(task);

        boolean isRemoved = manager.removeTaskByID(taskId);
        Task gottenTask = manager.getTaskByID(taskId);

        assertTrue(isRemoved, "Задача не удалена.");
        assertNull(gottenTask, "Задача существует.");
    }

    @Test
    void shouldRemoveSubtaskAndEmptyEpicAndReturnTrueIfItIsExist() {
        Epic epic = new Epic("Task 1", "Description 1");
        final int epicId = manager.createTask(epic);

        Subtask subtask = new Subtask(epicId, "Subtask 1", "Description 1", TaskStatus.NEW);
        final int subtaskID = manager.createTask(subtask);

        boolean isRemoved = manager.removeTaskByID(subtaskID);
        Task gottenSubtask = manager.getEpicByID(epicId);

        assertTrue(isRemoved, "Подзадача не удалена.");
        assertNull(gottenSubtask, "Подзадача существует.");

        Task gottenEpic = manager.getEpicByID(epicId);

        assertNull(gottenEpic, "Эпик без подзадач не удален.");
    }

    @Test
    void shouldRemoveEpicAndReturnTrueIfItIsExist() {
        Epic epic = new Epic("Epic 1", "Description 1");
        final int epicId = manager.createTask(epic);

        boolean isRemoved = manager.removeTaskByID(epicId);
        Task gottenEpic = manager.getEpicByID(epicId);

        assertTrue(isRemoved, "Эпик не удален.");
        assertNull(gottenEpic, "Эпик существует.");
    }

    @Test
    void shouldReturnFalseOnRemoveIfTaskIsNotExist() {
        boolean isRemoved = manager.removeTaskByID(1);
        assertFalse(isRemoved, "Подзадача удалена.");
    }

    @Test
    void shouldReturnCorrectListIfHistoryIsNotEmptyOrEmptyListIfHistoryIsEmpty() {
        List<Task> history = manager.getHistory();

        assertNotNull(history, "История не возвращается.");
        assertEquals(0, history.size(), "История не пуста.");

        Task task = new Task("Task 1", "Description 1", TaskStatus.NEW);
        final int taskId = manager.createTask(task);
        manager.getTaskByID(taskId);

        history = manager.getHistory();

        assertNotNull(history, "История не возвращается.");
        assertEquals(1, history.size(), "История не заполняется.");
        assertEquals(task, history.get(0), "Задачи не совпадают.");
    }

    @Test
    public void epicStatusShouldBeNewIfItIsEmpty() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = manager.createTask(epic);

        TaskStatus status = manager.getEpicByID(epicId).getStatus();
        assertEquals(TaskStatus.NEW, status, "Неверный статус эпика.");
    }

    @Test
    public void epicStatusShouldBeNewIfAllSubtasksIsNew() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = manager.createTask(epic);

        Subtask subtask1 = new Subtask(epicId, "Subtask 1", "Description", TaskStatus.NEW);
        manager.createTask(subtask1);

        TaskStatus epicStatus = manager.getEpicByID(epicId).getStatus();
        assertEquals(TaskStatus.NEW, epicStatus, "Неверный статус эпика.");
    }

    @Test
    public void epicStatusShouldBeDoneIfAllSubtasksIsDone() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = manager.createTask(epic);

        Subtask subtask1 = new Subtask(epicId, "Subtask 1", "Description", TaskStatus.DONE);
        manager.createTask(subtask1);

        TaskStatus epicStatus = manager.getEpicByID(epicId).getStatus();
        assertEquals(TaskStatus.DONE, epicStatus, "Неверный статус эпика.");
    }

    @Test
    public void epicStatusShouldBeInProgressIfSubtasksIsNewOrDone() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = manager.createTask(epic);

        Subtask subtask1 = new Subtask(epicId, "Subtask 1", "Description", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(epicId, "Subtask 2", "Description", TaskStatus.DONE);

        manager.createTask(subtask1);
        manager.createTask(subtask2);

        TaskStatus epicStatus = manager.getEpicByID(epicId).getStatus();
        assertEquals(TaskStatus.IN_PROGRESS, epicStatus, "Неверный статус эпика.");
    }

    @Test
    public void epicStatusShouldBeInProgressIfAllSubtasksIsInProgress() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = manager.createTask(epic);

        Subtask subtask1 = new Subtask(epicId, "Subtask 1", "Description", TaskStatus.IN_PROGRESS);
        manager.createTask(subtask1);

        TaskStatus epicStatus = manager.getEpicByID(epicId).getStatus();
        assertEquals(TaskStatus.IN_PROGRESS, epicStatus, "Неверный статус эпика.");
    }
}
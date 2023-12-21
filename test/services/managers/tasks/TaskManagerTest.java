package services.managers.tasks;

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.enums.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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

        boolean isRemoved = manager.removeTask(taskId);
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

        boolean isRemoved = manager.removeTask(subtaskID);
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

        boolean isRemoved = manager.removeTask(epicId);
        Task gottenEpic = manager.getEpicByID(epicId);

        assertTrue(isRemoved, "Эпик не удален.");
        assertNull(gottenEpic, "Эпик существует.");
    }

    @Test
    void shouldReturnFalseOnRemoveIfTaskIsNotExist() {
        boolean isRemoved = manager.removeTask(1);
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

    @Test
    public void shouldNotChangeTimeWithoutUpdate() {
        final int taskId = manager.createTask(new Task("Task 1", "Description 1", TaskStatus.NEW));
        Task task = manager.getTaskByID(taskId);

        task.setStartTime(LocalDateTime.now());
        task.setDuration(60);

        assertNotEquals(task, manager.getTaskByID(taskId), "Задача изменилась.");
    }

    @Test
    public void shouldChangeTimeOnUpdate() {
        final int taskId = manager.createTask(new Task("Task 1", "Description 1", TaskStatus.NEW));
        Task task = manager.getTaskByID(taskId);

        task.setStartTime(LocalDateTime.now());
        task.setDuration(60);

        manager.updateTask(taskId, task);

        assertEquals(task, manager.getTaskByID(taskId), "Задача не изменилась.");
    }

    @Test
    public void epicShouldBeWithoutTimeIfEmpty() {
        final int epicId = manager.createTask(new Epic("Epic 1", "Description 1"));
        Epic epic = manager.getEpicByID(epicId);

        assertEquals(0, epic.getSubtasksIDs().size(), "Эпик не пуст.");
        assertNull(epic.getStartTime(), "Время начала эпика не пустое.");
        assertEquals(0, epic.getDuration(), "Продолжительность эпика не равна 0.");
        assertNull(epic.getEndTime(), "Время завершения эпика не пустое.");
    }

    @Test
    public void epicShouldCalculateTimesCorrectly() {
        final int epicId = manager.createTask(new Epic("Epic 1", "Description 1"));

        Subtask subtask1 = new Subtask(epicId, "Subtask 1", "Description", TaskStatus.NEW);
        final int subtask1Id = manager.createTask(subtask1);

        Epic epic = manager.getEpicByID(epicId);

        assertNull(epic.getStartTime(), "Времена начала эпика не пустое.");
        assertEquals(0, epic.getDuration(), "Продолжительности эпика не равна 0.");
        assertNull(epic.getEndTime(), "Времена завершения эпика не пустое.");

        subtask1 = manager.getSubtaskByID(subtask1Id);
        subtask1.setStartTime(LocalDateTime.now());
        subtask1.setDuration(60);
        manager.updateTask(subtask1Id, subtask1);

        epic = manager.getEpicByID(epicId);

        assertEquals(subtask1.getStartTime(), epic.getStartTime(), "Времена начала эпика и подзадачи не совпадают.");
        assertEquals(subtask1.getDuration(), epic.getDuration(), "Продолжительности эпика и подзадачи не совпадают.");
        assertEquals(subtask1.getEndTime(), epic.getEndTime(), "Времена завершения эпика и подзадачи не совпадают.");

        Subtask subtask2 = new Subtask(epicId, "Subtask 2", "Description", TaskStatus.NEW);
        subtask2.setStartTime(LocalDateTime.now().plusHours(2));
        subtask2.setDuration(60);
        manager.createTask(subtask2);

        epic = manager.getEpicByID(epicId);

        assertEquals(
                subtask1.getStartTime(),
                epic.getStartTime(),
                "Времена начала эпика и первой подзадачи не совпадают."
        );
        assertEquals(
                subtask1.getDuration() + subtask2.getDuration(),
                epic.getDuration(),
                "Продолжительности эпика и подзадач не совпадают."
        );
        assertEquals(
                subtask2.getEndTime(),
                epic.getEndTime(),
                "Времена завершения эпика и последней подзадачи не совпадают."
        );
    }

    @Test
    public void shouldReturnEmptyPrioritizedList() {
        List<Task> prioritizedList = manager.getPrioritizedTasks();
        assertNotNull(prioritizedList, "Список не возвращается");
    }

    @Test
    public void shouldSortPrioritizeTasksAndReactToDeletionCorrectly() {
        Task task1 = new Task("Task1", "Description", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.now().plusHours(3));
        task1.setDuration(30);

        Task task2 = new Task("Task2", "Description", TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.now().plusHours(2));
        task2.setDuration(30);

        Task task3 = new Task("Task3", "Description", TaskStatus.NEW);
        task3.setStartTime(LocalDateTime.now().plusHours(1));
        task3.setDuration(30);

        Task task4 = new Task("Task4", "Description", TaskStatus.NEW);
        Task task5 = new Task("Task5", "Description", TaskStatus.NEW);

        final int task5Id = manager.createTask(task5);
        final int task1Id = manager.createTask(task1);
        manager.createTask(task4);
        manager.createTask(task3);
        manager.createTask(task2);

        List<Task> prioritizedList = manager.getPrioritizedTasks();

        assertNotNull(prioritizedList, "Список не возвращается");
        assertEquals(5, prioritizedList.size(), "Неправильная длинна списка.");
        assertEquals(task3, prioritizedList.get(0), "Задачи расположены в неправильном порядке.");
        assertEquals(task2, prioritizedList.get(1), "Задачи расположены в неправильном порядке.");
        assertEquals(task1, prioritizedList.get(2), "Задачи расположены в неправильном порядке.");
        assertEquals(task4, prioritizedList.get(3), "Задачи расположены в неправильном порядке.");
        assertEquals(task5, prioritizedList.get(4), "Задачи расположены в неправильном порядке.");

        manager.removeTask(task1Id);
        manager.removeTask(task5Id);

        prioritizedList = manager.getPrioritizedTasks();

        assertEquals(3, prioritizedList.size(), "Неправильная длинна списка.");
        assertEquals(task3, prioritizedList.get(0), "Задачи расположены в неправильном порядке.");
        assertEquals(task2, prioritizedList.get(1), "Задачи расположены в неправильном порядке.");
        assertEquals(task4, prioritizedList.get(2), "Задачи расположены в неправильном порядке.");

        manager.removeAllTasks();

        prioritizedList = manager.getPrioritizedTasks();

        assertEquals(0, prioritizedList.size(), "Неправильная длинна списка.");
    }

    @Test
    public void shouldAddTaskIfThereIsNotIntersectedInTime() {
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(60);

        int task1Id = manager.createTask(task1);
        assertNotEquals(-1, task1Id, "Задача не добавляется.");

        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW);
        task2.setStartTime(task1.getEndTime());
        task2.setDuration(60);

        int task2Id = manager.createTask(task2);
        assertNotEquals(-1, task2Id, "Задача не добавляется.");

        Task task3 = new Task("Task 3", "Description", TaskStatus.NEW);
        task3.setStartTime(task1.getStartTime().minusHours(1));
        task3.setDuration(60);

        int task3Id = manager.createTask(task3);
        assertNotEquals(-1, task3Id, "Задача не добавляется.");

        int task4Id = manager.createTask(new Task("Task 4", "Description", TaskStatus.NEW));
        assertNotEquals(-1, task4Id, "Задача не добавляется.");
    }

    @Test
    public void shouldNotAddTaskIfStartTimeIntersected() {
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(60);

        manager.createTask(task1);

        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW);
        task2.setStartTime(task1.getStartTime().plusMinutes(30));
        task2.setDuration(60);

        int task2Id = manager.createTask(task2);
        assertEquals(-1, task2Id, "Задача добавляется.");
    }

    @Test
    public void shouldNotAddTaskIfEndTimeIntersected() {
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(60);

        manager.createTask(task1);

        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW);
        task2.setStartTime(task1.getStartTime().minusMinutes(30));
        task2.setDuration(60);

        int task2Id = manager.createTask(task2);
        assertEquals(-1, task2Id, "Задача добавляется.");
    }

    @Test
    public void shouldNotAddTaskIfStartTimeAndEndTimeIntersected() {
        Task task1 = new Task("Task 1", "Description", TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(60);

        manager.createTask(task1);

        Task task2 = new Task("Task 2", "Description", TaskStatus.NEW);
        task2.setStartTime(task1.getStartTime().plusMinutes(15));
        task2.setDuration(30);

        int task2Id = manager.createTask(task2);

        assertEquals(-1, task2Id, "Задача добавляется.");

        task2.setStartTime(task1.getStartTime());
        task2Id = manager.createTask(task2);

        assertEquals(-1, task2Id, "Задача добавляется.");

        task2.setStartTime(task1.getEndTime().minusMinutes(30));
        task2Id = manager.createTask(task2);

        assertEquals(-1, task2Id, "Задача добавляется.");

        task2.setStartTime(task1.getStartTime().minusMinutes(30));
        task2.setDuration(120);
        task2Id = manager.createTask(task2);

        assertEquals(-1, task2Id, "Задача добавляется.");
    }
}
package services.managers.histories;

import models.business.Task;
import models.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    HistoryManager manager;

    @BeforeEach
    public void initManager() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    public void shouldHandleEmptyHistory() {
        List<Task> history = manager.getHistory();

        assertNotNull(history, "История не возвращается.");
        assertEquals(0, history.size(), "Неверная длинна истории.");

        boolean isDeleted = manager.remove(1);

        assertFalse(isDeleted, "Задача удалена.");
    }

    @Test
    public void shouldAddTask() {
        Task task = new Task("Task", "Description", TaskStatus.NEW);
        manager.add(task);

        assertEquals(1, manager.getHistory().size(), "Неверная длинна истории.");
        assertEquals(task, manager.getHistory().get(0), "Задачи не совпадают.");
    }

    @Test
    public void shouldRemoveFromStartOfHistory() {
        Task task1 = new Task("Task1", "Description", TaskStatus.NEW);
        Task task2 = new Task("Task2", "Description", TaskStatus.NEW);
        task1.setId(0);
        task2.setId(1);

        manager.add(task1);
        manager.add(task2);

        boolean isRemoved = manager.remove(0);

        List<Task> history = manager.getHistory();

        assertTrue(isRemoved, "Задача не удалена.");
        assertEquals(1, history.size(), "Неправильный размер истории.");
        assertEquals(task2, history.get(0), "Задачи не совпадают.");
    }

    @Test
    public void shouldRemoveFromEndOfHistory() {
        Task task1 = new Task("Task1", "Description", TaskStatus.NEW);
        Task task2 = new Task("Task2", "Description", TaskStatus.NEW);
        task1.setId(0);
        task2.setId(1);

        manager.add(task1);
        manager.add(task2);

        boolean isRemoved = manager.remove(1);

        List<Task> history = manager.getHistory();

        assertTrue(isRemoved, "Задача не удалена.");
        assertEquals(1, history.size(), "Неправильный размер истории.");
        assertEquals(task1, history.get(0), "Задачи не совпадают.");
    }

    @Test
    public void shouldRemoveFromMiddleOfHistory() {
        Task task1 = new Task("Task1", "Description", TaskStatus.NEW);
        Task task2 = new Task("Task2", "Description", TaskStatus.NEW);
        Task task3 = new Task("Task2", "Description", TaskStatus.NEW);
        task1.setId(0);
        task2.setId(1);
        task3.setId(2);

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        boolean isRemoved = manager.remove(1);

        List<Task> history = manager.getHistory();

        assertTrue(isRemoved, "Задача не удалена.");
        assertEquals(2, history.size(), "Неправильный размер истории.");
        assertEquals(task1, history.get(0), "Задачи не совпадают.");
        assertEquals(task3, history.get(1), "Задачи не совпадают.");
    }
}
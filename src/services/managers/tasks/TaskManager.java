package services.managers.tasks;

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    void removeAllTasks();

    Task getTaskByID(int id);

    Epic getEpicByID(int id);

    Subtask getSubtaskByID(int id);

    void createTask(Task task);

    void createTask(Subtask subtask);

    void createTask(Epic epic);

    void updateTask(Task task);

    void updateTask(Subtask subtask);

    void removeTaskByID(int id);

    List<Task> getHistory();
}

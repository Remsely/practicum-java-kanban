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

    int createTask(Task task);

    int createTask(Subtask subtask);

    int createTask(Epic epic);

    boolean updateTask(int id, Task task);

    boolean updateTask(int id, Subtask subtask);

    boolean removeTask(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}

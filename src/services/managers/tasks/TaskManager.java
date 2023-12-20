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

    int updateTask(Task task);

    int updateTask(Subtask subtask);

    boolean removeTaskByID(int id);

    List<Task> getHistory();
}

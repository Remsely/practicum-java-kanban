package services.manager;

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;

import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    public void removeAllTasks();

    Task getTaskByID(int id);

    Epic getEpicDyID(int id);

    Subtask getSubtaskByID(int id);

    void createTask(Task task);

    void createTask(Subtask subtask);

    void createTask(Epic epic);

    void updateTask(Task task);

    void updateTask(Subtask subtask);

    void removeTaskByID(int id);
}

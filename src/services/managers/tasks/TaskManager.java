package services.managers.tasks;

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    void clear();

    Task getTask(int id);

    Epic getEpic(int id);

    List<Subtask> getEpicSubtasks(int id);

    Subtask getSubtask(int id);

    int add(Task task);

    int add(Subtask subtask);

    int add(Epic epic);

    boolean update(int id, Task task);

    boolean update(int id, Subtask subtask);

    boolean remove(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.enums.TaskStatus;
import services.managers.tasks.TaskManager;
import services.managers.util.Managers;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        manager.createTask(new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW));
        manager.createTask(new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW));

        manager.createTask(new Epic("Эпик 1", "Описание эпика 1"));
        manager.createTask(new Subtask(2, "Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW));
        manager.createTask(new Subtask(2, "Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW));
        manager.createTask(new Subtask(2, "Подзадача 3", "Описание подзадачи 3", TaskStatus.NEW));

        manager.createTask(new Epic("Эпик 2", "Описание эпика 2"));

        System.out.println(manager);

        manager.getTaskByID(0);
        manager.getTaskByID(1);
        manager.getEpicByID(6);
        manager.getSubtaskByID(4);
        manager.getTaskByID(0);

        manager.getHistory();

        manager.getEpicByID(6);
        manager.getSubtaskByID(4);
        manager.getSubtaskByID(3);
        manager.getSubtaskByID(5);
        manager.getTaskByID(1);

        manager.getHistory();

        manager.removeTask(6);
        manager.removeTask(1);

        manager.getHistory();

        manager.removeTask(2);

        manager.getHistory();

        manager.getTaskByID(0);

        manager.getHistory();

        manager.removeTask(0);

        manager.getHistory();
    }
}

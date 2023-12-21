import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.enums.TaskStatus;
import services.managers.tasks.TaskManager;
import services.managers.util.Managers;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        manager.add(new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW));
        manager.add(new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW));

        manager.add(new Epic("Эпик 1", "Описание эпика 1"));
        manager.add(new Subtask(2, "Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW));
        manager.add(new Subtask(2, "Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW));
        manager.add(new Subtask(2, "Подзадача 3", "Описание подзадачи 3", TaskStatus.NEW));

        manager.add(new Epic("Эпик 2", "Описание эпика 2"));

        System.out.println(manager);

        manager.getTask(0);
        manager.getTask(1);
        manager.getEpic(6);
        manager.getSubtask(4);
        manager.getTask(0);

        manager.getHistory();

        manager.getEpic(6);
        manager.getSubtask(4);
        manager.getSubtask(3);
        manager.getSubtask(5);
        manager.getTask(1);

        manager.getHistory();

        manager.remove(6);
        manager.remove(1);

        manager.getHistory();

        manager.remove(2);

        manager.getHistory();

        manager.getTask(0);

        manager.getHistory();

        manager.remove(0);

        manager.getHistory();
    }
}

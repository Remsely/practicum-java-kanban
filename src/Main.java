import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.enums.TaskStatus;
import services.manager.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        manager.createTask(new Task(0, "Задача 1", "Описание задачи 1", TaskStatus.NEW));
        manager.createTask(new Task(1, "Задача 2", "Описание задачи 2", TaskStatus.NEW));

        manager.createTask(new Epic(2, "Эпик 1", "Опимание эпика 1"));
        manager.createTask(new Subtask(2, 3, "Подзадача 1", "Опиcание подзадачи 1", TaskStatus.NEW));
        manager.createTask(new Subtask(2, 4, "Подзадача 2", "Опиcание подзадачи 2", TaskStatus.NEW));

        manager.createTask(new Epic(5, "Эпик 2", "Опимание эпика 2"));
        manager.createTask(new Subtask(5, 6, "Подзадача 3", "Опиcание подзадачи 3", TaskStatus.NEW));

        System.out.println(manager);

        manager.updateTask(new Task(0, "Задача 1", "Описание задачи 1", TaskStatus.IN_PROGRESS));
        manager.updateTask(new Task(1, "Задача 2", "Описание задачи 2", TaskStatus.DONE));

        manager.updateTask(new Subtask(2, 3, "Подзадача 1", "Опиcание подзадачи 1", TaskStatus.IN_PROGRESS));
        manager.updateTask(new Subtask(2, 4, "Подзадача 2", "Опиcание подзадачи 2", TaskStatus.DONE));

        manager.updateTask(new Subtask(5, 6, "Подзадача 3", "Опиcание подзадачи 3", TaskStatus.DONE));

        System.out.println(manager);

        manager.removeTaskByID(4);
        manager.removeTaskByID(1);

        System.out.println(manager);

        manager.removeTaskByID(2);
        manager.removeTaskByID(0);

        System.out.println(manager);

        manager.removeTaskByID(6);

        System.out.println(manager);

        manager.removeTaskByID(6);
        manager.removeTaskByID(2);
        manager.removeTaskByID(0);

        System.out.println(manager);
    }
}

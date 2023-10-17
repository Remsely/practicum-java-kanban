import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

        manager.createTask(new Task(0, "Задача 1", "Описание задачи 1", "NEW"));
        manager.createTask(new Task(1, "Задача 2", "Описание задачи 2", "NEW"));

        manager.createTask(new Epic(2, "Эпик 1", "Опимание эпика 1"));
        manager.createTask(new Subtask(2, 3, "Подзадача 1", "Опиcание подзадачи 1", "NEW"));
        manager.createTask(new Subtask(2, 4, "Подзадача 2", "Опиcание подзадачи 2", "NEW"));

        manager.createTask(new Epic(5, "Эпик 2", "Опимание эпика 2"));
        manager.createTask(new Subtask(5, 6, "Подзадача 3", "Опиcание подзадачи 3", "NEW"));

        System.out.println(manager);

        manager.updateTask(new Task(0, "Задача 1", "Описание задачи 1", "IN_PROGRESS"));
        manager.updateTask(new Task(1, "Задача 2", "Описание задачи 2", "DONE"));

        manager.updateTask(new Subtask(2, 3, "Подзадача 1", "Опиcание подзадачи 1", "IN_PROGRESS"));
        manager.updateTask(new Subtask(2, 4, "Подзадача 2", "Опиcание подзадачи 2", "DONE"));

        manager.updateTask(new Subtask(5, 6, "Подзадача 3", "Опиcание подзадачи 3", "DONE"));

        System.out.println(manager);

        manager.removeTaskByID(4);
        manager.removeTaskByID(1);

        System.out.println(manager);

        manager.removeTaskByID(2);
        manager.removeTaskByID(0);

        System.out.println(manager);

        manager.removeTaskByID(6);

        System.out.println(manager);
    }
}

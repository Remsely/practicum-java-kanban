package services.managers.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.FileWriter;
import java.io.IOException;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private final static String EMPTY_FILE_PATH = "test/text_files/empty.csv";

    @BeforeEach
    public void initManager() {
        manager = new FileBackedTaskManager(EMPTY_FILE_PATH);
    }

    // Здесь придумал этот костыль, т. к. тесты ломаются из-за того, что все изменения в FileBackedTaskManager
    // сохраняются в файл. Это очень плохо, я правильно понимаю? Но это не обойти при таком подходе к тестам, только
    // если переопределять методы, но тогда смысла наследовать нет.
    @AfterEach
    public void cleanEmptyFile() {
        try (FileWriter writer = new FileWriter(EMPTY_FILE_PATH)) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
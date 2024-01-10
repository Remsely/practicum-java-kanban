package services.managers.tasks;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest {
    @BeforeEach
    public void initManager() {
        manager = new InMemoryTaskManager();
    }
}
package services.managers.tasks;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import services.http.servers.KVServer;
import services.managers.util.Managers;

import java.io.IOException;

public class HttpTaskManagerTest extends FileBackedTaskManagerTest {
    private static KVServer server;

    @BeforeAll
    public static void startServer() {
        try {
            server = new KVServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.start();
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @Override
    protected TaskManager getManager() {
        return Managers.getDefault();
    }
}

package services.managers.exceptions;

import java.io.IOException;

public class BackupFileReceivingException extends IOException {
    public BackupFileReceivingException(String message) {
        super(message);
    }
}
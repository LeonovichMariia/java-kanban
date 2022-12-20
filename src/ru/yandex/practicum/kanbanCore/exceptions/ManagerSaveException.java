package ru.yandex.practicum.kanbanCore.exceptions;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}


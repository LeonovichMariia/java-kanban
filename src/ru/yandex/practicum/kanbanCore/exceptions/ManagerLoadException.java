package ru.yandex.practicum.kanbanCore.exceptions;

public class ManagerLoadException extends RuntimeException {
    public ManagerLoadException (String message, Throwable cause) {
        super(message, cause);
    }
}

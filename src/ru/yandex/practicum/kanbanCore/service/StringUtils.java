package ru.yandex.practicum.kanbanCore.service;

import java.io.*;

public class StringUtils {
    public static boolean isBlank(String line) throws IOException {
        return line == null || line.isBlank();
    }
}
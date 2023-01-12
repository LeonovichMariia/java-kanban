package ru.yandex.practicum.kanbanCore.service;

import java.io.*;

public class StringUtils {
    public static boolean fileIsEmpty(String line) throws IOException {
        return line == null || line.isBlank();
    }
}
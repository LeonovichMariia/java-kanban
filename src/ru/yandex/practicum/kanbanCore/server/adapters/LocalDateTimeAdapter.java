package ru.yandex.practicum.kanbanCore.server.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter formatterWriter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
    private static final DateTimeFormatter formatterReader = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            jsonWriter.value("null");
        } else {
            jsonWriter.value(localDateTime.format(formatterWriter));
        }
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) {
        try {
            return LocalDateTime.parse(jsonReader.nextString(), formatterReader);
        }
        catch (Exception ex) {
            return null;
        }
    }
}
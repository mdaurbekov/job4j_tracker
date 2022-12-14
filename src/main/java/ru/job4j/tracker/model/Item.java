package ru.job4j.tracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Item {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @EqualsAndHashCode.Include
    private int id;
    @EqualsAndHashCode.Include
    private String name;

    private LocalDateTime created = LocalDateTime.now();

    public Item(String name) {
        this.name = name;
    }

}

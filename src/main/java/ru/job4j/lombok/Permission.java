package ru.job4j.lombok;


import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Builder
@Data
public class Permission {


    private int id;
    private String name;
    @Singular
    private List<String> rules;


    public static void main(String[] args) {
        var permission = Permission
                .builder()
                .id(1)
                .name("Имя")
                .rule("Строка1")
                .rule("Строка2")
                .build();
    }
}
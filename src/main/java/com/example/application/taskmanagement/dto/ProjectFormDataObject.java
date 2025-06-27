package com.example.application.taskmanagement.dto;

public record ProjectFormDataObject(String name) {
    public static final String PROP_NAME = "name";
    public static final int MAX_NAME_LENGTH = 255;
}

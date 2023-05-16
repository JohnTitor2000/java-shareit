package ru.practicum.shareit.exaption;

public class UnsupportedStatusException extends RuntimeException {
    public UnsupportedStatusException(String status) {
        super("Unknown state: " + status);
    }
}

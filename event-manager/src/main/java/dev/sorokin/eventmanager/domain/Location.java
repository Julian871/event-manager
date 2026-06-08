package dev.sorokin.eventmanager.domain;

import lombok.Data;

@Data
public class Location {

    private Integer id;

    private String name;

    private String address;

    private Integer capacity;

    private String description;
}

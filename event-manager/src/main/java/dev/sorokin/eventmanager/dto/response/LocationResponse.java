package dev.sorokin.eventmanager.dto.response;

import lombok.Data;

@Data
public class LocationResponse {

    private Integer id;

    private String name;

    private String address;

    private Integer capacity;

    private String description;
}

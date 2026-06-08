package dev.sorokin.eventmanager.mapper;

import dev.sorokin.eventmanager.domain.Location;
import dev.sorokin.eventmanager.dto.request.LocationRequest;
import dev.sorokin.eventmanager.dto.response.LocationResponse;
import dev.sorokin.eventmanager.entity.LocationEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocationMapper {

    public List<LocationResponse> toResponse(List<Location> locations) {
        return locations.stream()
                .map(this::toResponse)
                .toList();
    }

    public LocationResponse toResponse(Location location) {
        LocationResponse response = new LocationResponse();

        response.setId(location.getId());
        response.setName(location.getName());
        response.setAddress(location.getAddress());
        response.setCapacity(location.getCapacity());
        response.setDescription(location.getDescription()
        );

        return response;
    }

    public Location toDomain(LocationRequest locationRequest) {
        Location location = new Location();

        location.setId(null);
        location.setName(locationRequest.getName());
        location.setAddress(locationRequest.getAddress());
        location.setCapacity(locationRequest.getCapacity());
        location.setDescription(locationRequest.getDescription()
        );

        return location;
    }

    public Location toDomain(LocationEntity locationEntity) {
        Location location = new Location();

        location.setId(locationEntity.getId());
        location.setName(locationEntity.getName());
        location.setAddress(locationEntity.getAddress());
        location.setCapacity(locationEntity.getCapacity());
        location.setDescription(locationEntity.getDescription()
        );

        return location;
    }

    public LocationEntity toEntity(Location location) {
        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setName(location.getName());
        locationEntity.setAddress(location.getAddress());
        locationEntity.setCapacity(location.getCapacity());
        if(location.getDescription() != null && !location.getDescription().trim().isEmpty()) {
            locationEntity.setDescription(location.getDescription());
        }

        return locationEntity;
    }

    public void updateEntity(LocationEntity entity, Location location) {
        entity.setName(location.getName());
        entity.setAddress(location.getAddress());
        entity.setCapacity(location.getCapacity());
        if(location.getDescription() != null && !location.getDescription().trim().isEmpty()) {
            entity.setDescription(location.getDescription());
        }
    }
}

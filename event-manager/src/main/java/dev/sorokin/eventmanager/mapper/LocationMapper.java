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
        return new LocationResponse(
                location.id(),
                location.name(),
                location.address(),
                location.capacity(),
                location.description()
        );
    }

    public Location toDomain(LocationRequest locationRequest) {
        return new Location(
                null,
                locationRequest.getName(),
                locationRequest.getAddress(),
                locationRequest.getCapacity(),
                locationRequest.getDescription()
        );
    }

    public Location toDomain(LocationEntity locationEntity) {
        return new Location(
                locationEntity.getId(),
                locationEntity.getName(),
                locationEntity.getAddress(),
                locationEntity.getCapacity(),
                locationEntity.getDescription()
        );
    }

    public LocationEntity toEntity(Location location) {
        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setName(location.name());
        locationEntity.setAddress(location.address());
        locationEntity.setCapacity(location.capacity());

        if (location.description() != null && !location.description().trim().isEmpty()) {
            locationEntity.setDescription(location.description());
        }

        return locationEntity;
    }

    public void updateEntity(LocationEntity entity, Location location) {
        entity.setName(location.name());
        entity.setAddress(location.address());
        entity.setCapacity(location.capacity());
        if(location.description() != null && !location.description().trim().isEmpty()) {
            entity.setDescription(location.description());
        }
    }
}

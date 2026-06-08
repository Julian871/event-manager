package dev.sorokin.eventmanager.service.impl;

import dev.sorokin.eventmanager.domain.Location;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.exception.ApiException;
import dev.sorokin.eventmanager.mapper.LocationMapper;
import dev.sorokin.eventmanager.repository.LocationRepository;
import dev.sorokin.eventmanager.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @Override
    public List<Location> getLocations() {
        return locationRepository.findAll()
                .stream()
                .map(locationMapper::toDomain)
                .toList();
    }

    @Override
    public Location createLocation(Location location) {
        LocationEntity entity = locationMapper.toEntity(location);
        locationRepository.save(entity);

        return locationMapper.toDomain(entity);
    }

    @Override
    public void deleteLocation(int locationId) {
        if(!locationRepository.existsById(locationId)) throw new ApiException("Location not found", HttpStatus.NOT_FOUND);
        locationRepository.deleteById(locationId);
    }

    @Override
    public Location getLocationById(int locationId) {

        LocationEntity entity = locationRepository.findById(locationId).orElseThrow(
                () -> new ApiException("Location not found", HttpStatus.NOT_FOUND)
        );

        return locationMapper.toDomain(entity);
    }

    @Override
    public Location updateLocation(int locationId, Location location) {
        LocationEntity entity = locationRepository.findById(locationId).orElseThrow(
                () -> new ApiException("Location not found", HttpStatus.NOT_FOUND)
        );

        locationMapper.updateEntity(entity, location);
        locationRepository.save(entity);
        return locationMapper.toDomain(entity);
    }
}

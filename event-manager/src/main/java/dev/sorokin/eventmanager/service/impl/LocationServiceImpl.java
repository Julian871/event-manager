package dev.sorokin.eventmanager.service.impl;

import dev.sorokin.eventmanager.domain.Location;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.exception.ApiException;
import dev.sorokin.eventmanager.mapper.LocationMapper;
import dev.sorokin.eventmanager.repository.LocationRepository;
import dev.sorokin.eventmanager.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static dev.sorokin.eventmanager.redis.CacheConstants.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @Cacheable(
            value = CACHE_VALUE_LOCATIONS,
            key = "'all'",
            cacheManager = "locationListCacheManager"
    )
    @Override
    public List<Location> getLocations() {
        return locationRepository.findAll()
                .stream()
                .map(locationMapper::toDomain)
                .toList();
    }

    @Caching(evict = {
            @CacheEvict(
                    cacheNames = CACHE_VALUE_LOCATIONS,
                    key = "'all'",
                    cacheManager = "locationListCacheManager"
            ),
            @CacheEvict(
                    cacheNames = CACHE_VALUE_LOCATION,
                    key = "'id:' + #result.id()",
                    condition = "#result != null",
                    cacheManager = "locationListCacheManager"
            )
    })
    @Override
    public Location createLocation(Location location) {
        LocationEntity entity = locationMapper.toEntity(location);
        locationRepository.save(entity);

        return locationMapper.toDomain(entity);
    }

    @Caching(
            evict = {
                    @CacheEvict(
                            cacheNames = CACHE_VALUE_LOCATIONS,
                            key = "'all'"
                    ),
                    @CacheEvict(
                            cacheNames = CACHE_VALUE_LOCATION,
                            key = "'id:' + #locationId"
                    )
            }
    )
    @Override
    public void deleteLocation(Long locationId) {
        LocationEntity location = locationRepository.findById(locationId).orElseThrow(
                () -> new ApiException("Location not found", HttpStatus.NOT_FOUND)
        );

        if(!location.getEvents().isEmpty())
            throw new ApiException("Location has events", HttpStatus.BAD_REQUEST);

        locationRepository.deleteById(locationId);
    }

    @Cacheable(
            value = CACHE_VALUE_LOCATION,
            key = "'id:' + #locationId"
    )
    @Override
    public Location getLocationById(Long locationId) {

        LocationEntity entity = locationRepository.findById(locationId).orElseThrow(
                () -> new ApiException("Location not found", HttpStatus.NOT_FOUND)
        );

        if (!entity.getEvents().isEmpty())
            throw new ApiException("Cannot delete location with existing events", HttpStatus.BAD_REQUEST);

        return locationMapper.toDomain(entity);
    }

    @Caching(
            evict = {
                    @CacheEvict(
                            cacheNames = CACHE_VALUE_LOCATIONS,
                            key = "'all'"
                    ),
                    @CacheEvict(
                            cacheNames = CACHE_VALUE_LOCATION,
                            key = "'id:' + #locationId"
                    )
            }
    )
    @Override
    public Location updateLocation(Long locationId, Location location) {
        LocationEntity entity = locationRepository.findById(locationId).orElseThrow(
                () -> new ApiException("Location not found", HttpStatus.NOT_FOUND)
        );

        locationMapper.updateEntity(entity, location);
        locationRepository.save(entity);
        return locationMapper.toDomain(entity);
    }
}

package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.domain.Location;

import java.util.List;

public interface LocationService {

    List<Location> getLocations();

    Location createLocation(Location location);

    Location updateLocation(Long locationId, Location location);

    void deleteLocation(Long locationId);

    Location getLocationById(Long locationId);
}

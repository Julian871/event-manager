package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.domain.Location;

import java.util.List;

public interface LocationService {

    List<Location> getLocations();

    Location createLocation(Location location);

    void deleteLocation(int locationId);

    Location getLocationById(int locationId);

    Location updateLocation(int locationId, Location location);
}

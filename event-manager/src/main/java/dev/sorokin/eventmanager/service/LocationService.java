package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.domain.Location;
import dev.sorokin.eventmanager.dto.response.LocationResponse;

import java.util.List;

public interface LocationService {

    List<LocationResponse> getLocations();

    Location createLocation(Location location);

    void deleteLocation(int locationId);

    Location getLocationById(int locationId);

    Location updateLocation(int locationId, Location location);
}

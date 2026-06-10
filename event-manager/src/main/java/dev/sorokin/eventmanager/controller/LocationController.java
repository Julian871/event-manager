package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.domain.Location;
import dev.sorokin.eventmanager.dto.request.LocationRequest;
import dev.sorokin.eventmanager.dto.response.LocationResponse;
import dev.sorokin.eventmanager.mapper.LocationMapper;
import dev.sorokin.eventmanager.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    private final LocationMapper locationMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<LocationResponse> createLocation(@Valid @RequestBody LocationRequest locationRequest) {
        Location created = locationService.createLocation(locationMapper.toDomain(locationRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(locationMapper.toResponse(created));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<List<LocationResponse>> getAllLocations() {
        List<Location> locations = locationService.getLocations();
        return ResponseEntity.status(HttpStatus.OK).body(locationMapper.toResponse(locations));
    }

    @GetMapping("/{locationId}")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<LocationResponse> getLocationById(@PathVariable Long locationId) {
        Location location = locationService.getLocationById(locationId);
        return ResponseEntity.status(HttpStatus.OK).body(locationMapper.toResponse(location));
    }

    @PutMapping("/{locationId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<LocationResponse> updateLocation(
            @PathVariable Long locationId,
            @Valid @RequestBody LocationRequest locationRequest
            ) {
        Location updated = locationService.updateLocation(locationId, locationMapper.toDomain(locationRequest));
        return ResponseEntity.status(HttpStatus.OK).body(locationMapper.toResponse(updated));
    }

    @DeleteMapping("/{locationId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long locationId) {
        locationService.deleteLocation(locationId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

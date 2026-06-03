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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    private final LocationMapper locationMapper;

    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(@Valid @RequestBody LocationRequest locationRequest) {
        Location created = locationService.createLocation(locationMapper.toDomain(locationRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(locationMapper.toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<LocationResponse>> getAllLocations() {
        return ResponseEntity.status(HttpStatus.OK).body(locationService.getLocations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getLocationById(@PathVariable Integer id) {
        Location location = locationService.getLocationById(id);
        return ResponseEntity.status(HttpStatus.OK).body(locationMapper.toResponse(location));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationResponse> updateLocation(
            @PathVariable Integer id,
            @Valid @RequestBody LocationRequest locationRequest
            ) {
        Location updated = locationService.updateLocation(id, locationMapper.toDomain(locationRequest));
        return ResponseEntity.status(HttpStatus.OK).body(locationMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Integer id) {
        locationService.deleteLocation(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

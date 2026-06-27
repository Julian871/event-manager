package dev.sorokin.eventmanager.mapper;

import dev.sorokin.eventmanager.domain.Event;
import dev.sorokin.eventmanager.domain.EventSearch;
import dev.sorokin.eventmanager.dto.request.EventCreateRequest;
import dev.sorokin.eventmanager.dto.request.EventSearchRequest;
import dev.sorokin.eventmanager.dto.request.EventUpdateRequest;
import dev.sorokin.eventmanager.dto.response.EventResponse;
import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.entity.UserAccountEntity;
import dev.sorokin.eventmanager.enums.EventStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventMapper {

    public Event toDomainFromCreate(EventCreateRequest request) {
        return new Event(
                null,
                request.getName(),
                null,
                request.getMaxPlaces(),
                null,
                request.getDate(),
                request.getCost(),
                request.getDuration(),
                request.getLocationId(),
                EventStatus.WAIT_START
        );
    }

    public Event toDomainFromUpdate(EventUpdateRequest request) {
        return new Event(
                null,
                request.getName(),
                null,
                request.getMaxPlaces(),
                null,
                request.getDate(),
                request.getCost(),
                request.getDuration(),
                request.getLocationId(),
                EventStatus.WAIT_START
        );
    }

    public EventResponse toResponseFromEvent(Event event) {
        return new EventResponse(
                event.id(),
                event.name(),
                event.ownerId(),
                event.maxPlaces(),
                event.occupiedPlaces(),
                event.date(),
                event.cost(),
                event.duration(),
                event.locationId(),
                event.status().name()
        );
    }

    public List<EventResponse> toResponseFromEvent(List<Event> events) {
        return events.stream()
                .map(this::toResponseFromEvent)
                .toList();
    }

    public EventEntity toEntityFromCreate(Event event, UserAccountEntity user, LocationEntity location) {
        EventEntity entity = new EventEntity();
        entity.setName(event.name());
        entity.setStartAt(event.date());
        entity.setDurationMinutes(event.duration());
        entity.setMaxPlaces(event.maxPlaces());
        entity.setOccupiedPlaces(0);
        entity.setCost(event.cost());
        entity.setStatus(event.status());
        entity.setUser(user);
        entity.setLocation(location);

        return entity;
    }

    public Event toEventFromEntity(EventEntity entity, Long userId, Long locationId) {
        return new Event(
                entity.getId(),
                entity.getName(),
                userId,
                entity.getMaxPlaces(),
                entity.getOccupiedPlaces(),
                entity.getStartAt(),
                entity.getCost(),
                entity.getDurationMinutes(),
                locationId,
                entity.getStatus()
        );
    }

    public EventEntity toEntityFromUpdate(Event event, EventEntity existingEntity, LocationEntity location) {
        if (event.name() != null) {
            existingEntity.setName(event.name());
        }

        if (event.maxPlaces() != null) {
            existingEntity.setMaxPlaces(event.maxPlaces());
        }

        if (event.date() != null) {
            existingEntity.setStartAt(event.date());
        }

        if (event.cost() != null) {
            existingEntity.setCost(event.cost());
        }

        if (event.duration() != null) {
            existingEntity.setDurationMinutes(event.duration());
        }


        if (location != null) {
            existingEntity.setLocation(location);
        }

        return existingEntity;
    }

    public EventSearch toEventSearchFromRequest(EventSearchRequest request) {
        if(request == null) return null;
        return new EventSearch(
                request.getName(),
                request.getMinPlaces(),
                request.getMaxPlaces(),
                request.getDateStartAfter(),
                request.getDateStartBefore(),
                request.getCostMin(),
                request.getCostMax(),
                request.getDurationMin(),
                request.getDurationMax(),
                request.getLocationId(),
                request.getEventStatus()
        );
    }

}

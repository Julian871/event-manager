package dev.sorokin.eventmanager.service.impl;

import dev.sorokin.eventmanager.domain.Event;
import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.EventRegistrationEntity;
import dev.sorokin.eventmanager.entity.UserAccountEntity;
import dev.sorokin.eventmanager.enums.EventStatus;
import dev.sorokin.eventmanager.exception.ApiException;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.repository.EventRegistrationRepository;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.repository.UserRepository;
import dev.sorokin.eventmanager.service.EventRegistrationService;
import dev.sorokin.eventmanager.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventRegistrationServiceImpl implements EventRegistrationService {

    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public void eventRegistration(Long eventId) {
        EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(
                () -> new ApiException("Event not found", HttpStatus.NOT_FOUND)
        );

        if(eventEntity.getUser().getLogin().equals(securityUtil.getCurrentUserLogin()))
            throw new ApiException("Owner cannot register on his event", HttpStatus.BAD_REQUEST);

        if(!eventEntity.getStatus().equals(EventStatus.WAIT_START))
            throw new ApiException("Event has already started or finished or canceled", HttpStatus.BAD_REQUEST);

        if(Objects.equals(eventEntity.getMaxPlaces(), eventEntity.getOccupiedPlaces()))
            throw new ApiException("Capacity is insufficient", HttpStatus.BAD_REQUEST);

        UserAccountEntity userAccount = userRepository.findByLogin(securityUtil.getCurrentUserLogin()).orElseThrow(
                () -> new ApiException("User not found", HttpStatus.NOT_FOUND)
        );

        boolean alreadyRegistered = eventEntity.getRegistrations().stream()
                .anyMatch(r -> r.getUser().getId().equals(userAccount.getId()));

        if (alreadyRegistered) {
            throw new ApiException("User already registered for this event", HttpStatus.BAD_REQUEST);
        }

        int updatedCount = eventRepository.incrementOccupiedPlaces(eventId);

        if (updatedCount == 0) {
            throw new ApiException("No available places for this event", HttpStatus.BAD_REQUEST);
        }

        EventRegistrationEntity registration = new EventRegistrationEntity();
        registration.setEvent(eventEntity);
        registration.setUser(userAccount);

        eventRegistrationRepository.save(registration);
    }

    @Override
    @Transactional
    public void cancelEventRegistration(Long eventId) {
        EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(
                () -> new ApiException("Event not found", HttpStatus.NOT_FOUND)
        );

        if(!eventEntity.getStatus().equals(EventStatus.WAIT_START))
            throw new ApiException("Event has already started or finished or canceled", HttpStatus.BAD_REQUEST);

        UserAccountEntity user = userRepository.findByLogin(securityUtil.getCurrentUserLogin())
                .orElseThrow(
                        () -> new ApiException("User not found", HttpStatus.NOT_FOUND)
        );

        EventRegistrationEntity registration = eventRegistrationRepository
                .findByEventIdAndUserId(eventId, user.getId())
                .orElseThrow(
                        () -> new ApiException("Registration not found", HttpStatus.NOT_FOUND)
                );

        int updatedCount = eventRepository.decrementOccupiedPlaces(eventId);

        if (updatedCount == 0) {
            throw new ApiException("Failed to cancel registration due to inconsistent state",
                    HttpStatus.BAD_REQUEST);
        }

        eventRegistrationRepository.delete(registration);
    }

    @Override
    public List<Event> getMyRegistrations() {
        String userLogin = securityUtil.getCurrentUserLogin();

        List<EventRegistrationEntity> registrations = eventRegistrationRepository.findAllByUserLogin(userLogin);

        return registrations
                .stream()
                .map(registration -> eventMapper.toEventFromEntity(
                        registration.getEvent(),
                        registration.getUser().getId(),
                        registration.getEvent().getLocation().getId()
                ))
                .collect(Collectors.toList());
    }
}

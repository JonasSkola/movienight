package com.jonas.movienight.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.jonas.movienight.dto.EventDto;
import com.jonas.movienight.entity.UserEntity;
import com.jonas.movienight.repository.UserRepository;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas Karlsson on 2019-01-09.
 */

@RestController
public class GoogleController {

    private String CLIENT_ID = "861068135718-dnnfh5qp7jg5fegc78bd1e96ridu8qrp.apps.googleusercontent.com";

    private String CLIENT_SECRET = "66ERebuJzPmuOIS5Ux02VTe1";

    private final UserRepository userRepository;

    @Autowired
    public GoogleController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/storeauthcode")
    public String storeauthcode(Authentication authentication, @RequestBody String code,
                                @RequestHeader("X-Requested-With") String encoding) {
        if (encoding == null || encoding.isEmpty()) {
            return "Error, wrong headers";
        }

        String username = String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if (username == null || username.equals("anonymousUser")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        System.out.println(username);

        GoogleTokenResponse tokenResponse = null;
        try {
            tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    "https://www.googleapis.com/oauth2/v4/token",
                    CLIENT_ID,
                    CLIENT_SECRET,
                    code,
                    "http://localhost:8080")
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String email = null;
        try {
            email = tokenResponse.parseIdToken().getPayload().getEmail();
        } catch (IOException e) {
            e.printStackTrace();
        }

        UserEntity userEntity = userRepository.findByUsername(username);


        String accessToken = tokenResponse.getAccessToken();
        String refreshToken = tokenResponse.getRefreshToken();
        Long expiresAt = System.currentTimeMillis() + (tokenResponse.getExpiresInSeconds() * 1000);


        if (userEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        }

        userEntity.setEmail(email);
        userEntity.setAccessToken(accessToken);
        userEntity.setRefreshToken(refreshToken);
        userEntity.setExpiresAt(expiresAt);

        userRepository.save(userEntity);


        return "OK";
    }


    private List<Event> getEvents() {

        List<Event> existingEvents = new ArrayList<>();

        updateAccessTokens();

        List<UserEntity> users = userRepository.findAll();

        for (UserEntity user : users) {
            GoogleCredential credential = new GoogleCredential().setAccessToken(user.getAccessToken());
            Calendar calendar =
                    new Calendar.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                            .setApplicationName("Movie Nights")
                            .build();
            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = null;
            try {
                events = calendar.events().list("primary")
                        .setMaxResults(10)
                        .setTimeMin(now)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<Event> items = events.getItems();
            if (items != null) {
                existingEvents.addAll(items);
            }
        }
        return existingEvents;
    }


    private GoogleCredential getRefreshedCredentials(String refreshCode) {
        try {
            GoogleTokenResponse response = new GoogleRefreshTokenRequest(
                    new NetHttpTransport(), JacksonFactory.getDefaultInstance(), refreshCode, CLIENT_ID, CLIENT_SECRET)
                    .execute();

            return new GoogleCredential().setAccessToken(response.getAccessToken());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private void updateAccessTokens() {
        List<UserEntity> userEntities = userRepository.findAll();

        if (userEntities.size() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No users in database");
        }
        userEntities.forEach(u -> {
            long expiresAt = u.getExpiresAt();
            long currentTime = System.currentTimeMillis();

            if (checkToken(expiresAt, currentTime)) {
                GoogleCredential newAccessToken = getRefreshedCredentials(u.getRefreshToken());
                u.setAccessToken(newAccessToken.getAccessToken());
                u.setExpiresAt(currentTime);
                userRepository.save(u);
            }
        });
    }


    @GetMapping("/availibleTimes")
    public List<EventDto> getAvailibleTimes() {

        org.joda.time.DateTime now = new org.joda.time.DateTime();
        List<Event> takenTimes = getEvents();
        List<EventDto> availibleTimes = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            EventDto newEvent = new EventDto();
            newEvent.setStartTime(now.toString());
            availibleTimes.add(newEvent);
            now = now.plusDays(1);
        }

        takenTimes.forEach(e -> {
            int takenTime = convertStringToDateTime(e.getStart().getDateTime().toString()).getDayOfMonth();
            for (int i = 0; i < availibleTimes.size(); i++) {
                int freeTime = convertStringToDateTime(availibleTimes.get(i).getStartTime()).getDayOfMonth();
                if (takenTime == freeTime) {
                    availibleTimes.remove(i);
                }
            }
        });

        return availibleTimes;
    }

    private boolean checkToken(long expiresAt, long now) {
        org.joda.time.DateTime expireTime = new org.joda.time.DateTime(expiresAt);
        org.joda.time.DateTime currentTime = new org.joda.time.DateTime(now);
        return currentTime.isAfter(expireTime.plusHours(1));
    }

    private org.joda.time.DateTime convertStringToDateTime(String dateString) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        String startTimeString = dateString.substring(0, dateString.indexOf("T"));
        return formatter.parseDateTime(startTimeString);
    }

    @PostMapping("/createEvent")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@RequestBody EventDto eventDto) {

        if (eventDto.getStartTime() == null || eventDto.getEndTime() == null || eventDto.getMovieName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Event must contain start time, end time and movie name");
        }

        updateAccessTokens();

        List<UserEntity> users = userRepository.findAll();

        Event googleEvent = new Event();
        googleEvent.setStart(new EventDateTime().setDateTime(new DateTime(eventDto.getStartTime())));
        googleEvent.setEnd(new EventDateTime().setDateTime(new DateTime(eventDto.getEndTime())));
        googleEvent.setDescription(eventDto.getMovieName());
        googleEvent.setSummary("Movie time");

        for (UserEntity user : users) {
            GoogleCredential credential = new GoogleCredential().setAccessToken(user.getAccessToken());
            Calendar calendar =
                    new Calendar.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                            .setApplicationName("Movie Nights")
                            .build();
            try {
                calendar.events().insert(user.getEmail(), googleEvent).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return eventDto;
    }


}

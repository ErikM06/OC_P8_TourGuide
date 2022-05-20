package tourGuide.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import tourGuide.model.location.AttractionModel;
import tourGuide.model.location.VisitedLocationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tourGuide.customExceptions.UserNotFoundException;
import tourGuide.model.User;

import java.util.*;


@Service
public class GpsService {

    Logger logger = LoggerFactory.getLogger(GpsService.class);

    public VisitedLocationModel getUserLocationService(User user) throws UserNotFoundException {
        return trackUserLocation(user);
    }

    public List<AttractionModel> getAttractionsService() {
        RestTemplate restTemplate = new RestTemplate();
        String GPS_UTIL_SERVICE_API_ALL_ATTRACTION = "http://localhost:8090/getAllAttraction";
        ResponseEntity<List<AttractionModel>> result =
                restTemplate.exchange(
                        GPS_UTIL_SERVICE_API_ALL_ATTRACTION,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<AttractionModel>>() {}
                );
        List<AttractionModel> allAttractionModels = result.getBody();
        return allAttractionModels;
    }

    public VisitedLocationModel trackUserLocation(User user){

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        String GPS_UTIL_SERVICE_API_LAST_VISITED_LOCATION = "http://localhost:8090/getUserLastVisitedLocation";
        String USER_ID = "?userId=";
        ResponseEntity<VisitedLocationModel> result = restTemplate.getForEntity( GPS_UTIL_SERVICE_API_LAST_VISITED_LOCATION
                        + USER_ID
                        + user.getUserId().toString(),
                VisitedLocationModel.class);
        VisitedLocationModel trackedLocation = result.getBody();
        logger.debug("in trackUserLocation GPSservice");
        return trackedLocation;

    }

}

package tourGuide.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tourGuide.model.User;
import tourGuide.model.location.AttractionModel;
import tourGuide.model.location.VisitedLocationModel;

import java.util.Arrays;
import java.util.List;


@Service
public class GpsService {

    Logger logger = LoggerFactory.getLogger(GpsService.class);

    public VisitedLocationModel getUserLocationService(User user) {
        return trackUserLocation(user);
    }

    /**
     *
     * @return the list of all Attraction from the GpsUtil micro-service
     * @throws InvalidMediaTypeException
     */
    public List<AttractionModel> getAttractionsService() throws InvalidMediaTypeException {
        RestTemplate restTemplate = new RestTemplate();
        String GPS_UTIL_SERVICE_API_ALL_ATTRACTION = "http://gpsUtil:8090/getAllAttraction";
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

    /**
     *
     * @param user
     * @return a visitedLocationModel
     * user last visited location from the GpsUtil micro-service tracker
     * @throws InvalidMediaTypeException
     */
    public VisitedLocationModel trackUserLocation(User user) throws InvalidMediaTypeException{

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        String GPS_UTIL_SERVICE_API_LAST_VISITED_LOCATION = "http://gpsUtil:8090/getUserLastVisitedLocation";
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

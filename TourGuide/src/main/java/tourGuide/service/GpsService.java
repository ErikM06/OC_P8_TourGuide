package tourGuide.service;

import tourGuide.model.location.AttractionModel;
import tourGuide.model.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tourGuide.customExceptions.UserNotFoundException;
import tourGuide.model.User;

import java.util.List;
import java.util.Locale;
import java.util.UUID;


@Service
public class GpsService {

    Logger logger = LoggerFactory.getLogger(GpsService.class);

    private final String GPS_UTIL_SERVICE_API_LAST_VISITED_LOCATION = "http://localhost:8090/getUserLastVisitedLocation";
    private final String GPS_UTIL_SERVICE_API_ALL_ATTRACTION = "http://localhost:8090/getAllAttraction";

    private final String USER_ID ="?userId=";

    private UserService userService;

    public VisitedLocation getUserLocationService(UUID userId) throws UserNotFoundException {
       User user =  userService.getUserByUUID(userId);
        return trackUserLocation(user);
    }

    public List<AttractionModel> getAttractionsService() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<AttractionModel>> result =
                restTemplate.exchange(
                        GPS_UTIL_SERVICE_API_ALL_ATTRACTION,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<AttractionModel>>() {}
                );
        List<AttractionModel> allAttractionLModels = result.getBody();
        return allAttractionLModels;
    }

    public VisitedLocation trackUserLocation(User user){
        Locale.setDefault(Locale.US);
        RestTemplate restTemplate = new RestTemplate();
        VisitedLocation lastVisitedLocation = restTemplate.getForObject(GPS_UTIL_SERVICE_API_LAST_VISITED_LOCATION +
                USER_ID + user.getUserId().toString(),
                VisitedLocation.class);
        return lastVisitedLocation;

    }

}

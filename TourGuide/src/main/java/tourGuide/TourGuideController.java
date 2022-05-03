package tourGuide;

import com.jsoniter.output.JsonStream;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.customExceptions.UserNotFoundException;
import tourGuide.service.GpsService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.UserService;
import tripPricer.Provider;

import java.util.List;

@RestController
public class TourGuideController {

	@Autowired
	TourGuideService tourGuideService;

    @Autowired
    RewardsService rewardsService;

    @Autowired
    UserService userService;

    @Autowired
    GpsService gpsService;

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public String getLocation(@RequestParam String userName) throws UserNotFoundException {
    	VisitedLocation visitedLocation = gpsService.getUserLocationService(userService.getUserFromUserName(userName).getUserId());
		return JsonStream.serialize(visitedLocation.location);
    }

    @RequestMapping("/getNearbyAttractions") 
    public String getNearbyAttractions(@RequestParam String userName) throws UserNotFoundException {
    	VisitedLocation lastVisitedLocation = gpsService.trackUserLocation(userService.getUserFromUserName(userName));
    	return JsonStream.serialize(tourGuideService.getNearbyAttractionInfo(
                lastVisitedLocation,
                tourGuideService.getNearByAttractions(userService.getUserFromUserName(userName))
        ));
    }
    
    @RequestMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) throws UserNotFoundException {
    	return JsonStream.serialize(tourGuideService.getUserRewards(userService.getUserFromUserName(userName)));
    }
    
    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
    	return JsonStream.serialize(tourGuideService.getAllCurrentUserlastLocation(userService.getAllUsers()));
    }
    
    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) throws UserNotFoundException {
    	List<Provider> providers = tourGuideService.getTripDeals(userService.getUserFromUserName(userName));
    	return JsonStream.serialize(providers);
    }
    


}
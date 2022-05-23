package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.customExceptions.UserNotFoundException;
import tourGuide.model.ProviderModel;
import tourGuide.model.User;
import tourGuide.model.UserPreferences;
import tourGuide.model.UserReward;
import tourGuide.model.location.VisitedLocationModel;
import tourGuide.service.GpsService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.UserService;

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

    /**
     *
     * @param userName
     * @return LocationModel object
     * the last location of the user
     * @throws UserNotFoundException
     */
    @RequestMapping("/getLocation") 
    public String getLocation(@RequestParam String userName) throws UserNotFoundException {
    	VisitedLocationModel visitedLocationModel = gpsService.getUserLocationService(userService.getUserFromUserName(userName));
		return JsonStream.serialize(visitedLocationModel.locationModel);
    }

    /**
     *
     * @param userName
     * @return NearbyAttractionInfoDTO object
     * with user latest location latitude and longitude
     * the 5 closest attraction with the distance from user latest location
     * the reward for each of the 5 attractions
     * latitude and longitude of the 5 attractions
     * @throws UserNotFoundException
     */
    @RequestMapping("/getNearbyAttractions") 
    public String getNearbyAttractions(@RequestParam String userName) throws UserNotFoundException {
    	VisitedLocationModel lastVisitedLocationModel = gpsService.trackUserLocation(userService.getUserFromUserName(userName));
    	return JsonStream.serialize(tourGuideService.getNearbyAttractionInfo(
                lastVisitedLocationModel,
                tourGuideService.getNearByAttractions(userService.getUserFromUserName(userName))
        ));
    }

    /**
     *
     * @param userName
     * @return a list of UserReward
     * reward for each visited location
     * @throws UserNotFoundException
     */
    @RequestMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) throws UserNotFoundException {
        List<UserReward> userRewards = tourGuideService.getUserRewards(userService.getUserFromUserName(userName));
        if (userRewards.isEmpty()){
            return JsonStream.serialize("User "+userName+" have no rewards!");
        } else {
    	return JsonStream.serialize(userRewards);
        }
    }

    /**
     *
     * @return all users currrent location
     * with UUID as a string, latitude and longitude
     */
    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
    	return JsonStream.serialize(tourGuideService.getAllCurrentUserlastLocation(userService.getAllUsers()));
    }

    /**
     *
     * @param userName
     * @return a list of ProviderModel
     * with provider name
     * provider price for User preferences and rewards
     * the trip id
     *
     * @throws UserNotFoundException
     */
    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) throws UserNotFoundException {
    	List<ProviderModel> providers = tourGuideService.getTripDeals(userService.getUserFromUserName(userName));
    	return JsonStream.serialize(providers);
    }

    


}
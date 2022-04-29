package tourGuide.service.util;


import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearbyAttractionInfo {

    @Autowired
    TourGuideService tourGuideService;
    private final RewardsService rewardsService;

    public NearbyAttractionInfo(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    /**
     *
     * @param trackUser last user location
     * @param nearbyAttraction the 5 nearest attractions
     * @return a JSONObject with : Name of Tourist attraction,
     *     Tourist attractions lat/long,
     *     The user's location lat/long,
     *     The distance in miles between the user's location and each of the attractions.
     *     The reward points for visiting each Attraction.
     */
    public JSONObject getNearbyAttractionInfoAsJson (VisitedLocation trackUser, List<Attraction> nearbyAttraction){
        JSONObject attractionInfoAsJson = new JSONObject();
        Map<String,Double> mapOfAttractionDistance = new HashMap<>();
        Map<String,Integer> mapOfRewards= new HashMap<>();
        Map<String,JSONArray> mapOfAllLocations = new HashMap<>();
        Map<String,JSONArray> mapOfUserLocations = new HashMap<>();

        JSONArray userLocation = new JSONArray();

        nearbyAttraction.forEach(a -> {
            JSONArray attractionLocation = new JSONArray();
            attractionLocation.put(a.latitude);
            attractionLocation.put(a.longitude);

            mapOfAllLocations.put(a.attractionName,attractionLocation);

            mapOfAttractionDistance.put(a.attractionName, rewardsService.getDistance(trackUser.location,a));
            mapOfRewards.put(a.attractionName,rewardsService.getAttractionReward(a.attractionId,trackUser.userId));
        });

        userLocation.put(trackUser.location.latitude);
        userLocation.put(trackUser.location.longitude);
        mapOfUserLocations.put("for UserUUID: "+trackUser.userId.toString(),userLocation);

        attractionInfoAsJson.append("All attractions location : ",mapOfAllLocations);
        attractionInfoAsJson.append("User location is: ",mapOfUserLocations);
        attractionInfoAsJson.append("Attraction distances :", mapOfAttractionDistance);
        attractionInfoAsJson.append("Rewards of each attraction: ", mapOfRewards);

        return attractionInfoAsJson;
    }
}

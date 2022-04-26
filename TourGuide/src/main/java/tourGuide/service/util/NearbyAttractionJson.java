package tourGuide.service.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import tourGuide.model.User;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearbyAttractionJson {

    @Autowired
    TourGuideService tourGuideService;
    private final RewardsService rewardsService;

    public NearbyAttractionJson(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    public Map<String, JSONArray> getNearbyAttractionInfoAsJson (VisitedLocation trackUser, List<Attraction> nearbyAttraction){
        JSONObject attractionInfoAsJson = new JSONObject();
        Map<String, JSONArray> jsonArrayMap = new HashMap<>();
        JSONArray jsonArray = new JSONArray();


        nearbyAttraction.forEach(a -> {

           double distance = rewardsService.getDistance(a,trackUser.location);

           attractionInfoAsJson.put("Attraction name",a.attractionName)
                   .put("distance",distance)
                   .put(a.attractionName,rewardsService.getAttractionReward(trackUser.userId,a.attractionId));
            jsonArray.put(attractionInfoAsJson);
            jsonArrayMap.put(a.attractionName,jsonArray);
        });
        return jsonArrayMap;
    }
}

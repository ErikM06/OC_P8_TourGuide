package tourGuide.service.util;

import tourGuide.model.location.Attraction;
import tourGuide.model.location.Location;
import tourGuide.model.location.VisitedLocation;
import tourGuide.DTO.NearbyAttractionsInfoDTO;
import tourGuide.service.RewardsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearbyAttractionInfoAsJson {

    private final RewardsService rewardsService;

    public NearbyAttractionInfoAsJson(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    public NearbyAttractionsInfoDTO getNearbyAttractionInfoAsJson (VisitedLocation trackUser, List<Attraction> nearbyAttraction){
        NearbyAttractionsInfoDTO nearbyAttractionsInfoDTO = new NearbyAttractionsInfoDTO();
        Map<String,Double> mapOfAttractionDistance = new HashMap<>();
        Map<String,Integer> mapOfRewards= new HashMap<>();
        Map<String, Location> attractionLatLong = new HashMap<>();

        nearbyAttraction.forEach(a -> {

            attractionLatLong.put(a.attractionName,new Location(a.latitude,a.longitude));

            mapOfAttractionDistance.put(a.attractionName, rewardsService.getDistance(trackUser.location,a));
            mapOfRewards.put(a.attractionName,rewardsService.getAttractionReward(a.attractionId,trackUser.userId));
        });

        nearbyAttractionsInfoDTO.setUserLocationLatLong(trackUser.location);
        nearbyAttractionsInfoDTO.setAttractionLatLong(attractionLatLong);
        nearbyAttractionsInfoDTO.setAttractionDistanceFromUser(mapOfAttractionDistance);
        nearbyAttractionsInfoDTO.setRewardsForAttractions(mapOfRewards);

        return nearbyAttractionsInfoDTO;
    }
}

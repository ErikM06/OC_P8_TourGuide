package tourGuide.service.util;

import tourGuide.model.location.AttractionModel;
import tourGuide.model.location.LocationModel;
import tourGuide.model.location.VisitedLocationModel;
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

    public NearbyAttractionsInfoDTO getNearbyAttractionInfoAsJson (VisitedLocationModel trackUser, List<AttractionModel> nearbyAttractionModel){
        NearbyAttractionsInfoDTO nearbyAttractionsInfoDTO = new NearbyAttractionsInfoDTO();
        Map<String,Double> mapOfAttractionDistance = new HashMap<>();
        Map<String,Integer> mapOfRewards= new HashMap<>();
        Map<String, LocationModel> attractionLatLong = new HashMap<>();

        nearbyAttractionModel.forEach(a -> {

            attractionLatLong.put(a.attractionName,new LocationModel(a.latitude,a.longitude));

            mapOfAttractionDistance.put(a.attractionName, rewardsService.getDistance(trackUser.locationModel,a));
            mapOfRewards.put(a.attractionName,rewardsService.getRewardPoints(a.attractionId,trackUser.userId));
        });

        nearbyAttractionsInfoDTO.setUserLocationLatLong(trackUser.locationModel);
        nearbyAttractionsInfoDTO.setAttractionLatLong(attractionLatLong);
        nearbyAttractionsInfoDTO.setAttractionDistanceFromUser(mapOfAttractionDistance);
        nearbyAttractionsInfoDTO.setRewardsForAttractions(mapOfRewards);

        return nearbyAttractionsInfoDTO;
    }
}

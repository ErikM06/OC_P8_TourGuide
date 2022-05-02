package tourGuide.service.util;


import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.DTO.NearbyAttractionsInfoDTO;
import tourGuide.service.RewardsService;

import java.util.*;

public class NearbyAttractionInfo {


    private final RewardsService rewardsService;

    public NearbyAttractionInfo(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    /**
     *
     * @param trackUser last user location
     * @param nearbyAttraction the 5 nearest attractions
     * @return nearbyAttractionsInfoDTO
     * with : Name of Tourist attraction,
     *     Tourist attractions lat/long,
     *     The user's location lat/long,
     *     The distance in miles between the user's location and each of the attractions.
     *     The reward points for visiting each Attraction.
     */
    public NearbyAttractionsInfoDTO getNearbyAttractionInfoAsJson (VisitedLocation trackUser, List<Attraction> nearbyAttraction){
        NearbyAttractionsInfoDTO nearbyAttractionsInfoDTO = new NearbyAttractionsInfoDTO();
        Map<String,Double> mapOfAttractionDistance = new HashMap<>();
        Map<String,Integer> mapOfRewards= new HashMap<>();
        Map<String,ArrayList<Double>> attractionLatLong = new HashMap<>();


        nearbyAttraction.forEach(a -> {

            attractionLatLong.put(a.attractionName,new ArrayList<>(Arrays.asList(a.latitude,a.longitude)));

            mapOfAttractionDistance.put(a.attractionName, rewardsService.getDistance(trackUser.location,a));
            mapOfRewards.put(a.attractionName,rewardsService.getAttractionReward(a.attractionId,trackUser.userId));
        });

        nearbyAttractionsInfoDTO.setUserLocationLatLong(new ArrayList<>(Arrays.asList(trackUser.location.latitude,trackUser.location.longitude)));
        nearbyAttractionsInfoDTO.setAttractionLatLong(attractionLatLong);
        nearbyAttractionsInfoDTO.setAttractionDistanceFromUser(mapOfAttractionDistance);
        nearbyAttractionsInfoDTO.setRewardsForAttractions(mapOfRewards);

        return nearbyAttractionsInfoDTO;
    }
}

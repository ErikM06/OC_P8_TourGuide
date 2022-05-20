package tourGuide.DTO;


import tourGuide.model.location.LocationModel;

import java.util.HashMap;
import java.util.Map;

public class NearbyAttractionsInfoDTO {

    private Map<String, LocationModel> attractionLatLong = new HashMap<>();

    private LocationModel userLocationLatLong;

    private Map<String,Double> attractionDistanceFromUser = new HashMap<>();

     private Map<String,Integer> rewardsForAttractions = new HashMap<>();


    public NearbyAttractionsInfoDTO(LocationModel userLocationLatLong, Map<String, LocationModel> attractionLatLong, Map<String, Double> attractionDistanceFromUser, Map<String, Integer> rewardsForAttractions) {
        this.attractionLatLong = attractionLatLong;
        this.userLocationLatLong = userLocationLatLong;
        this.attractionDistanceFromUser = attractionDistanceFromUser;
        this.rewardsForAttractions = rewardsForAttractions;
    }
    public NearbyAttractionsInfoDTO(){

    }

    public LocationModel getUserLocationLatLong() {
        return userLocationLatLong;
    }

    public void setUserLocationLatLong(LocationModel userLocationLatLongModel) {
        this.userLocationLatLong = userLocationLatLongModel;
    }

    public Map<String, Double> getAttractionDistanceFromUser() {
        return attractionDistanceFromUser;
    }

    public void setAttractionDistanceFromUser(Map<String, Double> attractionDistanceFromUser) {
        this.attractionDistanceFromUser = attractionDistanceFromUser;
    }

    public Map<String, Integer> getRewardsForAttractions() {
        return rewardsForAttractions;
    }

    public void setRewardsForAttractions(Map<String, Integer> rewardsForAttractions) {
        this.rewardsForAttractions = rewardsForAttractions;
    }

    public Map<String, LocationModel> getAttractionLatLong() {
        return attractionLatLong;
    }

    public void setAttractionLatLong(Map<String, LocationModel> attractionLatLong) {
        this.attractionLatLong = attractionLatLong;
    }
}

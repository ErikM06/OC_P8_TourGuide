package tourGuide.DTO;


import gpsUtil.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NearbyAttractionsInfoDTO {

    private Map<String,Location> attractionLatLong = new HashMap<>();

    private Location userLocationLatLong;

    private Map<String,Double> attractionDistanceFromUser = new HashMap<>();

     private Map<String,Integer> rewardsForAttractions = new HashMap<>();


    public NearbyAttractionsInfoDTO(Location userLocationLatLong, Map<String,Location> attractionLatLong, Map<String, Double> attractionDistanceFromUser, Map<String, Integer> rewardsForAttractions) {
        this.attractionLatLong = attractionLatLong;
        this.userLocationLatLong = userLocationLatLong;
        this.attractionDistanceFromUser = attractionDistanceFromUser;
        this.rewardsForAttractions = rewardsForAttractions;
    }
    public NearbyAttractionsInfoDTO(){

    }

    public Location getUserLocationLatLong() {
        return userLocationLatLong;
    }

    public void setUserLocationLatLong(Location userLocationLatLong) {
        this.userLocationLatLong = userLocationLatLong;
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

    public Map<String, Location> getAttractionLatLong() {
        return attractionLatLong;
    }

    public void setAttractionLatLong(Map<String, Location> attractionLatLong) {
        this.attractionLatLong = attractionLatLong;
    }
}

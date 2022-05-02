package tourGuide.DTO;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NearbyAttractionsInfoDTO {

    private Map<String,ArrayList<Double>> attractionLatLong = new HashMap<>();

    private ArrayList<Double> userLocationLatLong;

    private Map<String,Double> attractionDistanceFromUser = new HashMap<>();

     private Map<String,Integer> rewardsForAttractions = new HashMap<>();


    public NearbyAttractionsInfoDTO(ArrayList<Double> userLocationLatLong, Map<String,ArrayList<Double>> attractionLatLong, Map<String, Double> attractionDistanceFromUser, Map<String, Integer> rewardsForAttractions) {
        this.attractionLatLong = attractionLatLong;
        this.userLocationLatLong = userLocationLatLong;
        this.attractionDistanceFromUser = attractionDistanceFromUser;
        this.rewardsForAttractions = rewardsForAttractions;
    }
    public NearbyAttractionsInfoDTO(){

    }

    public ArrayList<Double> getUserLocationLatLong() {
        return userLocationLatLong;
    }

    public void setUserLocationLatLong(ArrayList<Double> userLocationLatLong) {
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

    public Map<String, ArrayList<Double>> getAttractionLatLong() {
        return attractionLatLong;
    }

    public void setAttractionLatLong(Map<String, ArrayList<Double>> attractionLatLong) {
        this.attractionLatLong = attractionLatLong;
    }
}

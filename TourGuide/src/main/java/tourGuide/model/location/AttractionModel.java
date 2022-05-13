package tourGuide.model.location;

import com.jsoniter.annotation.JsonProperty;

import java.util.UUID;

public class AttractionModel extends Location {

    public String attractionName;
    public String city;
    public String state;
    public UUID attractionId;

    public AttractionModel(String attractionName, String city, String state, double latitude, double longitude) {
        super(latitude, longitude);
        this.attractionName = attractionName;
        this.city = city;
        this.state = state;
        this.attractionId = UUID.randomUUID();
    }
    public AttractionModel (){

    }

}


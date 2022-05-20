package tourGuide.model.location;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.UUID;

public class VisitedLocationModel {

    public UUID userId;
    @JsonProperty("location")
    public LocationModel locationModel;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date timeVisited;

    public VisitedLocationModel(UUID userId, LocationModel locationModel, Date timeVisited) {
        this.userId = userId;
        this.locationModel = locationModel;
        this.timeVisited = timeVisited;
    }
    public VisitedLocationModel(){

    }
}


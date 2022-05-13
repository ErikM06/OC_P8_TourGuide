package tourGuide.model;

import java.util.UUID;

public class ProviderModel {

    public String name;
    public double price;
    public UUID tripId;

    public ProviderModel(UUID tripId, String name, double price) {
        this.name = name;
        this.tripId = tripId;
        this.price = price;
    }
    public ProviderModel (){

    }
}


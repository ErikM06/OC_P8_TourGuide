package tourGuide.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tourGuide.model.ProviderModel;
import tourGuide.repository.InternalTestService;
import tourGuide.model.User;



import java.util.List;
import java.util.UUID;

@Service
public class TripDealsService {
    private Logger logger = LoggerFactory.getLogger(TripDealsService.class);

    public List<ProviderModel> getTripDeals(User user) {
        int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
        List<ProviderModel> providers = getProvidersFromHTTP(InternalTestService.tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
        user.setTripDeals(providers);
        logger.debug("in TripDealsService , user infos : adults "+user.getUserPreferences().getNumberOfAdults()+" childrens "
                +user.getUserPreferences().getNumberOfChildren()+ " tripDuration "+user.getUserPreferences().getTripDuration());

        return providers;
    }
    public List<ProviderModel> getProvidersFromHTTP (String apiKey, UUID attractionId, int adults, int children, int nightsStay,
                                                     int rewardsPoints){

    RestTemplate restTemplate = new RestTemplate();
    String URL_FOR_GET_PROVIDER ="http://localhost:9010/getProvider";
    String URI_API="apiKey=";
    String URI_ATTRACTION_ID="attractionId=";
    String URI_NUMBER_ADULT="adults=";
    String URI_NUMBER_CHILDREN="children=";
    String URI_NUMBER_NIGHT="nightsStay=";
    String URI_REWARD_POINTS="rewardsPoints=";

    ResponseEntity<List<ProviderModel>> response =restTemplate.exchange(URL_FOR_GET_PROVIDER
                    +"?"
                    +URI_API+apiKey
                    +"&"+URI_ATTRACTION_ID+attractionId
                    +"&"+URI_NUMBER_ADULT+adults
                    +"&"+URI_NUMBER_CHILDREN+children
                    +"&"+URI_NUMBER_NIGHT+nightsStay
                    +"&"+URI_REWARD_POINTS+rewardsPoints,
            HttpMethod.GET,null,
            new ParameterizedTypeReference<List<ProviderModel>>() {});

        return response.getBody();
}
}

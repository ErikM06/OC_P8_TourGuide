package tourGuide.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.repository.InternalTestService;
import tourGuide.model.User;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.List;

@Service
public class TripDealsService {
    private Logger logger = LoggerFactory.getLogger(TripDealsService.class);
    private final TripPricer tripPricer = new TripPricer();

    public List<Provider> getTripDeals(User user) {
        int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
        List<Provider> providers = tripPricer.getPrice(InternalTestService.tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
        user.setTripDeals(providers);
        logger.debug("in TripDealsService , user infos : adults "+user.getUserPreferences().getNumberOfAdults()+" childrens "
                +user.getUserPreferences().getNumberOfChildren()+ " tripDuration "+user.getUserPreferences().getTripDuration());

        return providers;
    }
}

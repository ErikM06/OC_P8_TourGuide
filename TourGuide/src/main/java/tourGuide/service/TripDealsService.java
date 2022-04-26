package tourGuide.service;

import org.springframework.stereotype.Service;
import tourGuide.repository.InternalTestService;
import tourGuide.model.User;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.List;

@Service
public class TripDealsService {
    private final TripPricer tripPricer = new TripPricer();

    public List<Provider> getTripDeals(User user) {
        int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
        List<Provider> providers = tripPricer.getPrice(InternalTestService.tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }
}

package tourGuide.service.util;

import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.model.User;
import tourGuide.service.GpsService;
import tourGuide.service.RewardsService;

import java.util.Locale;
import java.util.function.Supplier;


public class ThreadTrackUserLocation implements Runnable {

    Logger logger = LoggerFactory.getLogger(ThreadTrackUserLocation.class);

    private final GpsService gpsService;

    private final RewardsService rewardsService;

    private final User user;

    public ThreadTrackUserLocation(GpsService gpsService, RewardsService rewardsService, User user) {
        this.gpsService = gpsService;
        this.rewardsService = rewardsService;
        this.user = user;
    }

    @Override
    public void run() {
        Locale.setDefault(Locale.US);
        VisitedLocation visitedLocation = gpsService.trackUserLocation(user);
        user.addToVisitedLocations(visitedLocation);
        rewardsService.calculateRewards(user);
        logger.debug("in runnable : "+ user.getLastVisitedLocation());
    }
}


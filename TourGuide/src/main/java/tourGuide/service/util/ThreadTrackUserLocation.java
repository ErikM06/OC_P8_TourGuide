package tourGuide.service.util;

import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.model.User;
import tourGuide.service.GpsService;

import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.function.Supplier;


public class ThreadTrackUserLocation implements Supplier<VisitedLocation> {

    Logger logger = LoggerFactory.getLogger(ThreadTrackUserLocation.class);

    private final GpsService gpsService;

    private final User user;

    public ThreadTrackUserLocation(GpsService gpsService, User user) {
        this.gpsService = gpsService;
        this.user = user;
    }


    @Override
    public VisitedLocation get() {
        Locale.setDefault(Locale.US);
        VisitedLocation visitedLocation = gpsService.trackUserLocation(user);
        user.addToVisitedLocations(visitedLocation);
        return visitedLocation;
    }
}


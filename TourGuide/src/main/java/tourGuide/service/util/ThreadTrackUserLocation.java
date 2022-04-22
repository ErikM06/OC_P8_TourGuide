package tourGuide.service.util;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.service.GpsService;
import tourGuide.user.User;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.*;


public class ThreadTrackUserLocation implements  Callable<VisitedLocation>{

    Logger logger = LoggerFactory.getLogger(ThreadTrackUserLocation.class);

    private final GpsUtil gpsUtil;

    private final User user;

    public ThreadTrackUserLocation(GpsUtil gpsUtil, User user) {
        this.gpsUtil = gpsUtil;
        this.user = user;
    }

    @Override
    public VisitedLocation call() throws Exception {
        VisitedLocation visitedLocation = gpsUtil.getUserLocation(this.user.getUserId());
        return visitedLocation;
    }


}


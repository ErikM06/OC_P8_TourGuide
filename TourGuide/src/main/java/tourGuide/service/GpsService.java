package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.service.util.ThreadTrackUserLocation;
import tourGuide.user.User;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Service
public class GpsService {

    Logger logger = LoggerFactory.getLogger(GpsService.class);

    private final GpsUtil gpsUtil;

    public GpsService (GpsUtil gpsUtil){
        this.gpsUtil = gpsUtil;
    }


    public VisitedLocation getUserLocationService(UUID userId) {
        return gpsUtil.getUserLocation(userId);
    }

    public List<Attraction> getAttractionsService() {
        return gpsUtil.getAttractions();
    }

    public VisitedLocation trackUserLocation(User user){
        ThreadTrackUserLocation threadTrackUserLocation = new ThreadTrackUserLocation(gpsUtil,user);
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<VisitedLocation> future = executorService.submit(threadTrackUserLocation);

        try {
            logger.debug("in trackUserLcoation :"+ future.get());
            return future.get();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }




}

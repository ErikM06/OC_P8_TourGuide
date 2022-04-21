package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.DAO.UserDao;
import tourGuide.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

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
}

package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.DAO.UserDao;
import tourGuide.customExceptions.UserNotFoundException;
import tourGuide.user.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class GpsService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RewardsService rewardsService;

    private final GpsUtil gpsUtil;

    public GpsService (GpsUtil gpsUtil){
        this.gpsUtil = gpsUtil;
    }

    public VisitedLocation getUserLocation(User user) throws UserNotFoundException {
        VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
                user.getLastVisitedLocation() :
                trackUserLocation(user);
        return visitedLocation;
    }

    public VisitedLocation trackUserLocation(User user) throws UserNotFoundException {
        VisitedLocation visitedLocation = userDao.getUserFromUserName(user.getUserName()).getLastVisitedLocation();
        user.addToVisitedLocations(visitedLocation);
        rewardsService.calculateRewards(user);
        return visitedLocation;
    }
    public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
        List<Attraction> nearbyAttractions = new ArrayList<>();
        for (Attraction attraction : gpsUtil.getAttractions()) {
            if (rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
                nearbyAttractions.add(attraction);
            }
        }

        return nearbyAttractions;
    }
}

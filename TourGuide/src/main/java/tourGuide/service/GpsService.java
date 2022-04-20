package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.DAO.UserDao;
import tourGuide.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class GpsService {

    @Autowired
    private UserDao userDao;


    private final RewardsService rewardsService;

    private final GpsUtil gpsUtil;

    public GpsService (RewardsService rewardsService, GpsUtil gpsUtil){
        this.rewardsService = rewardsService;
        this.gpsUtil = gpsUtil;
    }

    public VisitedLocation getUserLocation(User user){
        VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
                user.getLastVisitedLocation() :
                trackUserLocation(user);
        return visitedLocation;
    }

    public VisitedLocation trackUserLocation(User user){
      //  VisitedLocation visitedLocation = userDao.getUserFromUserName(user.getUserName()).getLastVisitedLocation();
        VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
        user.addToVisitedLocations(visitedLocation);
        rewardsService.calculateRewards(user);
        return visitedLocation;
    }
    public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
        List<Attraction> nearbyAttractions = new ArrayList<>();
        List<Attraction> allAttractions = new CopyOnWriteArrayList<>(gpsUtil.getAttractions());
        allAttractions.forEach(a-> {
            if (rewardsService.isWithinAttractionProximity(a, visitedLocation.location)) {
                nearbyAttractions.add(a);
            }
        });

        return nearbyAttractions;
    }
}

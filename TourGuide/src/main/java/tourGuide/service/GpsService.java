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
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class GpsService {
    Logger logger = LoggerFactory.getLogger(GpsService.class);
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

    /**
     *
     * @param visitedLocation
     * @return List<Attraction> nearbyAttractions
     * Get ordred List<Double> attractionDistances
     * which is getDistance between visitedLocation and allAttractions attractions
     * then reverse the List en delete record to keep only the 5 closest distances
     */
    public List<Attraction> getNearByAttractions(User user) {
        List<Attraction> nearbyAttractions = new ArrayList<>();
        List<Attraction> allAttractions = new CopyOnWriteArrayList<>(gpsUtil.getAttractions());
        VisitedLocation lastVisitedLocation = getUserLocation(user);



      /**
       * allAttractions.forEach(a-> {
            if (rewardsService.isWithinAttractionProximity(a, visitedLocation.location)) {
                        nearbyAttractions.add(a);
                    }
        });
       */

        List<Double> attractionDistances = new ArrayList<>();
        allAttractions.forEach(a-> {
            Double distance = rewardsService.getDistance(a, lastVisitedLocation.location);
            attractionDistances.add(distance);
        });

        attractionDistances.sort(Collections.reverseOrder());
       while ( attractionDistances.size()>5){
            int i=0;
            logger.debug("in loop "+attractionDistances.get(i) +" last of list"+attractionDistances.get(5));
            attractionDistances.remove(i);
        }
        Collections.sort(attractionDistances);
        logger.debug("list first in list is"  + attractionDistances.get(0) + " last in list is "+attractionDistances.get(4)+" size is "+attractionDistances.size());
        attractionDistances.forEach( d -> {
            allAttractions.forEach(a ->{
                if (d == rewardsService.getDistance(a, lastVisitedLocation.location)){
                    nearbyAttractions.add(a);
                        }
                    }
                    );
        });
        return nearbyAttractions;
    }

}

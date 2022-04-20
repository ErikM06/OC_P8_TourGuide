package tourGuide.service;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.DAO.UserDao;
import tourGuide.customExceptions.UserNotFoundException;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	public final UserDao userDao;

	public final GpsService gpsService;

	public final TripDealsService tripDealsService;
	boolean testMode = true;

	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService, UserDao userDao, GpsService gpsService, TripDealsService tripDealsService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		this.userDao = userDao;
		this.gpsService = gpsService;
		this.tripDealsService = tripDealsService;

		if (testMode) {
		userDao.initializeFromInternalTestService();
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	public VisitedLocation getUserLocationFromService(User user){

		return gpsService.getUserLocation(user);
	}

	/**
	 * public User getUser(String userName) throws UserNotFoundException {
	 *	return internalTestService.getUser(userName);
	}*/

	public List<User> getAllUsersFromDao() {
		return userDao.getAllUsers();
	}

	public void addNewUser (User user){
		userDao.addUser(user);
	}

	public List<Provider> getTripDeals(User user) {
		return tripDealsService.getTripDeals(user);
	}

	public VisitedLocation trackUserLocation(User user) {
		return gpsService.trackUserLocation(user);
	}

	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		return gpsService.getNearByAttractions(visitedLocation);
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				tracker.stopTracking();
			}
		});
	}

}

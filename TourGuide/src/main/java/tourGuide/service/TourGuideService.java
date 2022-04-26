package tourGuide.service;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.DAO.UserDao;
import tourGuide.service.util.ThreadTrackUserLocation;
import tourGuide.tracker.Tracker;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final RewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	public final UserDao userDao;

	public final GpsService gpsService;

	public final TripDealsService tripDealsService;
	boolean testMode = true;

	public TourGuideService(RewardsService rewardsService, UserDao userDao, GpsService gpsService, TripDealsService tripDealsService) {
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

	public VisitedLocation getUserLocation(User user){
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
				user.getLastVisitedLocation() :
				trackUserLocation(user);
		return visitedLocation;
	}

	public void trackUserLocation(User user){
		ThreadTrackUserLocation threadTrackUserLocation = new ThreadTrackUserLocation(gpsService, user);
		ExecutorService executorService = Executors.newFixedThreadPool(100);
		CompletableFuture<VisitedLocation> completableFuture = CompletableFuture.supplyAsync(threadTrackUserLocation);
		CompletableFuture<Void> future = completableFuture.thenRun(new Runnable() {
			@Override
			public void run() {
				rewardsService.calculateRewards(user);
			}
		});
	}

	/**
	 *
	 * @param user
	 * @return List<Attraction> nearbyAttractions
	 * Get ordred List<Double> attractionDistances
	 * which is getDistance between visitedLocation and allAttractions attractions
	 * then reverse the List en delete record to keep only the 5 closest distances
	 */
	public List<Attraction> getNearByAttractions(User user) {
		List<Attraction> nearbyAttractions = new ArrayList<>();
		List<Attraction> allAttractions = new CopyOnWriteArrayList<>(gpsService.getAttractionsService());
		VisitedLocation lastVisitedLocation = getUserLocation(user);

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
		logger.debug("in List<Attraction> getNearByAttractions(User user), nearbyAttractions list size is : "+nearbyAttractions.size());
		return nearbyAttractions;
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

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				tracker.stopTracking();
			}
		});
	}

}

package tourGuide.service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.customExceptions.UserNotFoundException;
import tourGuide.repository.InternalTestService;
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

	public final GpsService gpsService;

	private  final InternalTestService internalTestService;
	public final TripDealsService tripDealsService;
	boolean testMode = true;

	public TourGuideService(RewardsService rewardsService, GpsService gpsService, InternalTestService internalTestService, TripDealsService tripDealsService) {
		this.rewardsService = rewardsService;
		this.gpsService = gpsService;
		this.internalTestService = internalTestService;
		this.tripDealsService = tripDealsService;

		if (testMode) {

			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			this.internalTestService.initializeInternalUsers();
			logger.debug("Finished initializing users");

		}
		tracker = new Tracker(this);
		addShutDownHook();
	}
	ExecutorService executorService = Executors.newFixedThreadPool(100);

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}


	public VisitedLocation getUserLocation(User user){
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
				user.getLastVisitedLocation() :
				trackUserLocation(user);
		return visitedLocation;
	}

	public VisitedLocation trackUserLocation(User user) {
		Locale.setDefault(Locale.US);
		VisitedLocation visitedLocation = gpsService.trackUserLocation(user);
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		logger.debug("in trackUserLocation, visitedLocation : "+ visitedLocation);
		return visitedLocation;
	}

	public void runTrackUser (List<User> userLs) {
		userLs.forEach (u -> {
			CompletableFuture<Void> completableFutur;
			completableFutur = CompletableFuture.runAsync(() -> trackUserLocation(u), executorService)
					.exceptionally(throwable -> {
						logger.debug("Something went wrong");
						return null;
					});
				}
		);
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

	public List<Provider> getTripDeals(User user) {
		return tripDealsService.getTripDeals(user);
	}

	public User getUserFromUserName (String userName) throws UserNotFoundException {
		return internalTestService.getUser(userName);
	}

	public List<User> getAllUsers() {
		return internalTestService.internalUserMap.values().stream().collect(Collectors.toList());
	}

	public void addUser(User user) {
		if (!internalTestService.internalUserMap.containsKey(user.getUserName())) {
			internalTestService.internalUserMap.put(user.getUserName(), user);
		}
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				tracker.stopTracking();
			}
		});
	}



}

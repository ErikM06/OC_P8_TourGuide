package tourGuide.service;

import java.util.*;
import java.util.concurrent.*;

import tourGuide.model.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tourGuide.model.location.Attraction;
import tourGuide.model.location.VisitedLocation;
import tourGuide.DTO.NearbyAttractionsInfoDTO;
import tourGuide.repository.InternalTestService;
import tourGuide.service.util.NearbyAttractionInfoAsJson;
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

	private final UserService userService;
	boolean testMode = true;

	ExecutorService executorService = Executors.newFixedThreadPool(1000);

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public TourGuideService(RewardsService rewardsService, GpsService gpsService, InternalTestService internalTestService, TripDealsService tripDealsService, UserService userService) {
		this.rewardsService = rewardsService;
		this.gpsService = gpsService;
		this.internalTestService = internalTestService;
		this.tripDealsService = tripDealsService;
		this.userService = userService;

		if (testMode) {

			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			this.internalTestService.initializeInternalUsers();
			logger.debug("Finished initializing users");

		}
		tracker = new Tracker(this, this.userService);
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

	public VisitedLocation trackUserLocation(User user) {
		VisitedLocation visitedLocation = gpsService.trackUserLocation(user);
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	public void runTrackUser (User user) {

		CompletableFuture<Void> completableFuture = CompletableFuture
				.runAsync(() -> trackUserLocation(user), executorService)
				.exceptionally(throwable -> {
					logger.debug("Something went wrong in runTrackUser");
					return null;
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
			attractionDistances.remove(i);
		}
		Collections.sort(attractionDistances);
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
	 * @param trackUser last user location
	 * @param nearbyAttraction the 5 nearest attractions
	 * @return nearbyAttractionsInfoDTO
	 * with : Name of Tourist attraction,
	 *     Tourist attractions lat/long,
	 *     The user's location lat/long,
	 *     The distance in miles between the user's location and each of the attractions.
	 *     The reward points for visiting each Attraction.
	 */
	public NearbyAttractionsInfoDTO getNearbyAttractionInfo(VisitedLocation trackUser, List<Attraction> nearbyAttraction){
		NearbyAttractionInfoAsJson nearbyAttractionsInfoDTO = new NearbyAttractionInfoAsJson(rewardsService );
		return nearbyAttractionsInfoDTO.getNearbyAttractionInfoAsJson(trackUser,nearbyAttraction);
	}

	public List<Provider> getTripDeals(User user) {
		return tripDealsService.getTripDeals(user);
	}


	/**
	 *
	 * @param allUsers
	 * @return @return List<Map<String, Location>> getAllCurrentUserlastLocation.
	 * For each User in list allUser :
	 * instantiate a map with user's UUID to string and lastLocation.location.
	 * list is returned.
	 */
	public List<Map<String, Location>> getAllCurrentUserlastLocation (List<User> allUsers){
		List<Map<String,Location>> allCurrentUserLastLocationLs = new ArrayList<>();
		allUsers.forEach(u -> {
			Map<String,Location> userIdForLocation = new HashMap<>();
			userIdForLocation.put(u.getUserId().toString(),u.getLastVisitedLocation().location);
			allCurrentUserLastLocationLs.add(userIdForLocation);
		});
		return  allCurrentUserLastLocationLs;
	}


	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				tracker.stopTracking();
			}
		});
	}



}

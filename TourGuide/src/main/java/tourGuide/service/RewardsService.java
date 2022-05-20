package tourGuide.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.model.location.AttractionModel;
import tourGuide.model.location.LocationModel;
import tourGuide.model.location.VisitedLocationModel;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class RewardsService {

	private Logger logger = LoggerFactory.getLogger(RewardsService.class);
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
	private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsService gpsService;


	ExecutorService executorService = Executors.newFixedThreadPool(1000);

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public RewardsService(GpsService gpsService) {
		this.gpsService = gpsService;
	}

	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	/**
	 *
	 * @param user
	 * async method to run getUserRewards with simulated high stress from InternalTestHelper
	 */
	public void asyncTaskCalculateRewards (User user){
		CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() ->
						calculateRewards(user), executorService)
				.exceptionally(throwable -> {
					logger.debug("Something went wrong in asyncTaskCalculateRewards");
					return null;
				});
	}

	/**
	 *
	 * @param user
	 * Calculate user rewards for each visitedLocation if it is near an Attraction
	 * a is AttractionModel
	 * l is VisistedLocationModel
	 */
	public void calculateRewards(User user) throws NullPointerException {
		List<AttractionModel> attractionModels = new CopyOnWriteArrayList<>( gpsService.getAttractionsService());
		List<VisitedLocationModel> userLocations = new CopyOnWriteArrayList<>(user.getVisitedLocations());
		logger.info("in RewardService : calculateRewards");
		userLocations.forEach(l -> {
			attractionModels.forEach(a -> {
				if (nearAttraction(l, a)) {
					if (user.getUserRewards().stream().noneMatch(r -> r.attractionModel.attractionName.equals(a.attractionName))) {
						user.addUserReward(new UserReward(l, a, getRewardPoints(a.attractionId, user.getUserId())));
					}
				}
			});
		});
	}

	public boolean isWithinAttractionProximity(AttractionModel attractionModel, LocationModel locationModel) {
		return getDistance(attractionModel, locationModel) > attractionProximityRange ? false : true;
	}

	private boolean nearAttraction(VisitedLocationModel visitedLocationModel, AttractionModel attractionModel) {
		return getDistance(attractionModel, visitedLocationModel.locationModel) > proximityBuffer ? false : true;
	}

	/**
	 *
	 * @param attractionId
	 * @param userID
	 * @return a random int generated in RewardsCentral micro-service
	 * @throws InvalidMediaTypeException
	 */
	public Integer getRewardPoints(UUID attractionId, UUID userID) throws InvalidMediaTypeException {
		String URL_TO_REWARD_POINT = "http://localhost:9000/getRewardPoint";
		String URI_ATTRACTION_UUID = "?attractionId=";
		String AND = "&";
		String URI_USER_UUID ="userId=";

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Integer> response = restTemplate.getForEntity(URL_TO_REWARD_POINT
						+URI_ATTRACTION_UUID+attractionId.toString()
						+AND
						+URI_USER_UUID+userID.toString()
				,Integer.class);
		return response.getBody();
	}

	/**
	 *
	 * @param loc1
	 * @param loc2
	 * @return a double the distance between loc1 and loc2
	 */
	public double getDistance(LocationModel loc1, LocationModel loc2) {
		double lat1 = Math.toRadians(loc1.latitude);
		double lon1 = Math.toRadians(loc1.longitude);
		double lat2 = Math.toRadians(loc2.latitude);
		double lon2 = Math.toRadians(loc2.longitude);

		double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

		double nauticalMiles = 60 * Math.toDegrees(angle);
		double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
		return statuteMiles;
	}

}

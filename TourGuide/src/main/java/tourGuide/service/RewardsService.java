package tourGuide.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.model.User;
import tourGuide.model.UserReward;

@Service
public class RewardsService {

	private Logger logger = LoggerFactory.getLogger(RewardsService.class);
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
	private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsService gpsService;
	private final RewardCentral rewardsCentral;

	ExecutorService executorService = Executors.newFixedThreadPool(1000);

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public RewardsService(GpsService gpsService, RewardCentral rewardCentral) {
		this.gpsService = gpsService;
		this.rewardsCentral = rewardCentral;
	}

	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	public void asyncTaskCalculateRewards (User user){
		CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(new Runnable() {
			@Override
			public void run() {
				calculateRewards(user);
			}
		}, executorService);

	}

	public void calculateRewards(User user) {
		List<Attraction> attractions = new CopyOnWriteArrayList<>( gpsService.getAttractionsService());
		List<VisitedLocation> userLocations = new CopyOnWriteArrayList<>(user.getVisitedLocations());

		userLocations.forEach(l -> {
			attractions.forEach(a -> {
				if (nearAttraction(l, a)) {
					if (user.getUserRewards().stream().noneMatch(r -> r.attraction.attractionName.equals(a.attractionName))) {
						user.addUserReward(new UserReward(l, a, getRewardPoints(a, user)));
					}
				}
			});
		});
	}

	public int getAttractionReward (UUID attractionId, UUID userId){
		return rewardsCentral.getAttractionRewardPoints(attractionId,userId);
	}
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}

	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}

	private int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}

	public double getDistance(Location loc1, Location loc2) {
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

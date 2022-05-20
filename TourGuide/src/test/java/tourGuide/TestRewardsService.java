package tourGuide;



import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;





import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.location.AttractionModel;
import tourGuide.model.location.LocationModel;
import tourGuide.repository.InternalTestService;
import tourGuide.service.*;
import tourGuide.model.User;
import tourGuide.model.UserReward;

import tourGuide.model.location.VisitedLocationModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRewardsService {
	Logger logger = LoggerFactory.getLogger(TestRewardsService.class);

	@Autowired

	@BeforeAll
	private static void setUp (){
		Locale.setDefault(Locale.US);
	}
	@Test
	public void userGetRewards(){
		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService();
		UserService userService = new UserService(internalTestService);
		RewardsService rewardsService = new RewardsService(gpsService);
		TripDealsService tripDealsService = new TripDealsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService, userService);

		InternalTestHelper.setInternalUserNumber(100);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		AttractionModel attractionLoc = gpsService.getAttractionsService().get(0);
		// create a new locationModel with the lat and long of a known attraction to be sure that user lastloc and attraction location are equal
		LocationModel locationModelFromAttraction = new LocationModel(attractionLoc.latitude,attractionLoc.longitude);

		user.addToVisitedLocations(new VisitedLocationModel(user.getUserId(), locationModelFromAttraction, new Date()));

		List<UserReward> userRewards = tourGuideService.getUserRewards(user);
		tourGuideService.tracker.stopTracking();
		logger.debug(" in @Test userGetReward(), reward point is :"+userRewards.get(0).getRewardPoints());

		assertEquals(1, userRewards.size());
	}
	
	@Test
	public void isWithinAttractionProximity() {
		GpsService gpsService = new GpsService();
		RewardsService rewardsService = new RewardsService(gpsService);
		AttractionModel attraction = gpsService.getAttractionsService().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}
	
	// Needs fixed - can throw ConcurrentModificationException
	@Test
	public void nearAllAttractions() {

		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService();
		UserService userService = new UserService(internalTestService);
		RewardsService rewardsService = new RewardsService(gpsService);
		TripDealsService tripDealsService = new TripDealsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService, userService);

		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(100);

		
		rewardsService.calculateRewards(userService.getAllUsers().get(0));
		List<UserReward> userRewards = tourGuideService.getUserRewards(userService.getAllUsers().get(0));
		tourGuideService.tracker.stopTracking();

		assertEquals(gpsService.getAttractionsService().size(), userRewards.size());
	}
	
}

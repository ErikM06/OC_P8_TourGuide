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
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		TripDealsService tripDealsService = new TripDealsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService, userService);

		InternalTestHelper.setInternalUserNumber(0);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		AttractionModel attraction = gpsService.getAttractionsService().get(0);


		user.addToVisitedLocations(new VisitedLocationModel(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		tourGuideService.tracker.stopTracking();
		logger.debug(" in @Test userGetReword(), reward point is :"+userRewards.get(0).getRewardPoints());

		assertEquals(1, userRewards.size());
	}
	
	@Test
	public void isWithinAttractionProximity() {
		GpsService gpsService = new GpsService();
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		AttractionModel attraction = gpsService.getAttractionsService().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}
	
	// Needs fixed - can throw ConcurrentModificationException
	@Test
	public void nearAllAttractions() {

		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService();
		UserService userService = new UserService(internalTestService);
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		TripDealsService tripDealsService = new TripDealsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService, userService);

		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);

		
		rewardsService.calculateRewards(userService.getAllUsers().get(0));
		List<UserReward> userRewards = tourGuideService.getUserRewards(userService.getAllUsers().get(0));
		tourGuideService.tracker.stopTracking();

		assertEquals(gpsService.getAttractionsService().size(), userRewards.size());
	}
	
}

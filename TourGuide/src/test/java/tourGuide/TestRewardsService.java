package tourGuide;



import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;



import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rewardCentral.RewardCentral;
import tourGuide.DAO.UserDao;
import tourGuide.helper.InternalTestHelper;
import tourGuide.repository.InternalTestService;
import tourGuide.service.GpsService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.TripDealsService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRewardsService {
	Logger logger = LoggerFactory.getLogger(TestRewardsService.class);

	@BeforeAll
	private static void setUp (){
		Locale.setDefault(Locale.US);
	}
	@Test
	public void userGetRewards(){
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(gpsUtil);
		TripDealsService tripDealsService = new TripDealsService();
		UserDao userDao=new UserDao(internalTestService);
		TourGuideService tourGuideService = new TourGuideService(rewardsService, userDao, gpsService, tripDealsService);
		InternalTestHelper.setInternalUserNumber(0);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		tourGuideService.tracker.stopTracking();
		logger.debug(" in @Test userGetReword(), reward point is :"+userRewards.get(0).getRewardPoints());

		assertEquals(1, userRewards.size());
	}
	
	@Test
	public void isWithinAttractionProximity() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		Attraction attraction = gpsUtil.getAttractions().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}
	
	// Needs fixed - can throw ConcurrentModificationException
	@Test
	public void nearAllAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(gpsUtil);
		TripDealsService tripDealsService = new TripDealsService();
		UserDao userDao=new UserDao(internalTestService);
		TourGuideService tourGuideService = new TourGuideService(rewardsService, userDao, gpsService, tripDealsService);

		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);

		
		rewardsService.calculateRewards(userDao.getAllUsers().get(0));
		List<UserReward> userRewards = tourGuideService.getUserRewards(userDao.getAllUsers().get(0));
		tourGuideService.tracker.stopTracking();

		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
	}
	
}

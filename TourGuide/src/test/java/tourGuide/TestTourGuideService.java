package tourGuide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rewardCentral.RewardCentral;
import tourGuide.DAO.UserDao;
import tourGuide.customExceptions.UserNotFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.repository.InternalTestService;
import tourGuide.service.GpsService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.TripDealsService;
import tourGuide.user.User;
import tripPricer.Provider;

public class TestTourGuideService {

	private final Logger logger = LoggerFactory.getLogger(TestTourGuideService.class);

	@Test
	public void getUserLocation() throws UserNotFoundException {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(gpsUtil);
		TripDealsService tripDealsService = new TripDealsService();
		UserDao userDao=new UserDao(internalTestService);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, userDao, gpsService, tripDealsService);
		InternalTestHelper.setInternalUserNumber(0);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}
	
	@Test
	public void addUser() throws UserNotFoundException {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(gpsUtil);
		TripDealsService tripDealsService = new TripDealsService();
		UserDao userDao=new UserDao(internalTestService);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, userDao, gpsService, tripDealsService);
		InternalTestHelper.setInternalUserNumber(0);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userDao.addUser(user);
		userDao.addUser(user2);
		
		User retrivedUser = userDao.getUserFromUserName(user.getUserName());
		User retrivedUser2 = userDao.getUserFromUserName(user2.getUserName());

		tourGuideService.tracker.stopTracking();
		
		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}
	
	@Test
	public void getAllUsers() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(gpsUtil);
		TripDealsService tripDealsService = new TripDealsService();
		UserDao userDao=new UserDao(internalTestService);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, userDao, gpsService, tripDealsService);
		InternalTestHelper.setInternalUserNumber(0);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userDao.addUser(user);
		userDao.addUser(user2);
		
		List<User> allUsers = userDao.getAllUsers();

		tourGuideService.tracker.stopTracking();
		
		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}
	
	@Test
	public void trackUser() throws UserNotFoundException {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(gpsUtil);
		TripDealsService tripDealsService = new TripDealsService();
		UserDao userDao=new UserDao(internalTestService);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, userDao, gpsService, tripDealsService);
		InternalTestHelper.setInternalUserNumber(0);

		Map<String, User> internalUserMap = internalTestService.internalUserMap;
		Object internalUser = internalUserMap.keySet().toArray()[new Random().nextInt(internalUserMap.keySet().toArray().length)];
		User user1 = internalUserMap.get(internalUser);
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user1);
		logger.debug("in test trackUser(); user is:"+user1.getUserName());
		tourGuideService.tracker.stopTracking();
		
		assertEquals(user1.getUserId(), visitedLocation.userId);
	}
	
	@Ignore // Not yet implemented
	@Test
	public void getNearbyAttractions() throws UserNotFoundException {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(gpsUtil);
		TripDealsService tripDealsService = new TripDealsService();
		UserDao userDao=new UserDao(internalTestService);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, userDao, gpsService, tripDealsService);
		InternalTestHelper.setInternalUserNumber(0);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		
		List<Attraction> attractions = tourGuideService.getNearByAttractions(visitedLocation);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(5, attractions.size());
	}
	
	public void getTripDeals() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(gpsUtil);
		TripDealsService tripDealsService = new TripDealsService();
		UserDao userDao=new UserDao(internalTestService);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, userDao, gpsService, tripDealsService);
		InternalTestHelper.setInternalUserNumber(0);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideService.getTripDeals(user);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(10, providers.size());
	}
	
	
}

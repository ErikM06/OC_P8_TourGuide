package tourGuide;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;




import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TestTourGuideService {

	private final Logger logger = LoggerFactory.getLogger(TestTourGuideService.class);

	@BeforeAll
	private static void setUp (){
		Locale.setDefault(Locale.US);
	}
	@Test
	public void getUserLocation() throws UserNotFoundException {
		logger.debug("Local is :" +Locale.getDefault());
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(rewardsService, gpsUtil);
		TripDealsService tripDealsService = new TripDealsService();
		UserDao userDao=new UserDao(internalTestService);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, userDao, gpsService, tripDealsService);
		InternalTestHelper.setInternalUserNumber(0);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertEquals(visitedLocation.userId, user.getUserId());
	}
	
	@Test
	public void addUser() throws UserNotFoundException {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(rewardsService, gpsUtil);
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
		GpsService gpsService = new GpsService(rewardsService, gpsUtil);
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
		GpsService gpsService = new GpsService(rewardsService, gpsUtil);
		TripDealsService tripDealsService = new TripDealsService();
		UserDao userDao=new UserDao(internalTestService);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, userDao, gpsService, tripDealsService);
		InternalTestHelper.setInternalUserNumber(0);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		logger.debug("in test trackUser(); user is:"+user.getUserName());
		tourGuideService.tracker.stopTracking();
		
		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	/**
	 *
	 * @throws UserNotFoundException
	 * Location location latitude and longitude are close to Disneyland (33.817595, -117.922008)
	 * When using tracker, long and lat are often negative and can't find a nearbyAttractions
	 */
	@Test
	public void getNearbyAttractions() throws UserNotFoundException {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(rewardsService, gpsUtil);
		TripDealsService tripDealsService = new TripDealsService();
		UserDao userDao=new UserDao(internalTestService);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, userDao, gpsService, tripDealsService);
		InternalTestHelper.setInternalUserNumber(0);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		// VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		Location location = new Location(33.000000,-117.000000);
		Date date = new Date(System.currentTimeMillis());
		VisitedLocation visitedLocation =new VisitedLocation(user.getUserId(),location, date);

		List<Attraction> attractions = tourGuideService.getNearByAttractions(visitedLocation);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(4, attractions.size());
	}
	@Test
	public void getTripDeals() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(rewardsService, gpsUtil);
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

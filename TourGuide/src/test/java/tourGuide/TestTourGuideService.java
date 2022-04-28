package tourGuide;


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
import tourGuide.customExceptions.UserNotFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.repository.InternalTestService;
import tourGuide.service.GpsService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.TripDealsService;
import tourGuide.model.User;
import tourGuide.model.UserPreferences;
import tripPricer.Provider;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TestTourGuideService {

	final Logger logger = LoggerFactory.getLogger(TestTourGuideService.class);

	@BeforeAll
	static void setUp (){
		Locale.setDefault(Locale.US);
	}
	@Test
	public void getUserLocation(){
		logger.debug("Local is :" +Locale.getDefault());
		GpsUtil gpsUtil = new GpsUtil();
		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(gpsUtil);
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		TripDealsService tripDealsService = new TripDealsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService);
		InternalTestHelper.setInternalUserNumber(0);

		tourGuideService.tracker.stopTracking();

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);

		assertEquals(visitedLocation.userId, user.getUserId());
	}

	@Test
	public void addUser() throws UserNotFoundException {
		GpsUtil gpsUtil = new GpsUtil();

		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(gpsUtil);
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		TripDealsService tripDealsService = new TripDealsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService);


		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);

		User retrivedUser = tourGuideService.getUserFromUserName(user.getUserName());
		User retrivedUser2 = tourGuideService.getUserFromUserName(user2.getUserName());

		tourGuideService.tracker.stopTracking();

		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}

	@Test
	public void getAllUsers() {
		GpsUtil gpsUtil = new GpsUtil();

		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(gpsUtil);
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		TripDealsService tripDealsService = new TripDealsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);

		List<User> allUsers = tourGuideService.getAllUsers();

		tourGuideService.tracker.stopTracking();

		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}

	@Test
	public void trackUser(){

		GpsUtil gpsUtil = new GpsUtil();
		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(gpsUtil);
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		TripDealsService tripDealsService = new TripDealsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		int firstVisitedLocationSize = user.getVisitedLocations().size();
		tourGuideService.trackUserLocation(user);

		logger.debug("in test trackUser(); user is:"+user.getUserName());
		tourGuideService.tracker.stopTracking();

		assertNotEquals(firstVisitedLocationSize, user.getVisitedLocations().size());
	}

	/**
	 * Location location latitude and longitude are close to Disneyland (33.817595, -117.922008)
	 * When using tracker, long and lat are often negative and can't find a nearbyAttractions
	 */
	@Test
	public void getNearbyAttractions(){
		GpsUtil gpsUtil = new GpsUtil();

		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(gpsUtil);
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		TripDealsService tripDealsService = new TripDealsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService);


		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		// VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		Location location = new Location(33.000000,-117.000000);
		Date date = new Date(System.currentTimeMillis());
		VisitedLocation visitedLocation =new VisitedLocation(user.getUserId(),location, date);

		List<Attraction> attractions = tourGuideService.getNearByAttractions(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(5, attractions.size());
	}

	/**
	 * in this test the new user=user don't have any
	 */
	@Test
	public void getTripDeals() {
		GpsUtil gpsUtil = new GpsUtil();
		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(gpsUtil);
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		TripDealsService tripDealsService = new TripDealsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService);


		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		UserPreferences userPreferencesTest = new UserPreferences();
		userPreferencesTest.setTripDuration(1);
		userPreferencesTest.setNumberOfAdults(1);
		userPreferencesTest.setNumberOfChildren(1);

		user.setUserPreferences(userPreferencesTest);

		List<Provider> providers = tourGuideService.getTripDeals(user);
		logger.debug("in TripDealsTest for provider :"+providers.get(0).name+ "price is : "+providers.get(0).price);

		tourGuideService.tracker.stopTracking();

		assertEquals(5, providers.size());
	}


}

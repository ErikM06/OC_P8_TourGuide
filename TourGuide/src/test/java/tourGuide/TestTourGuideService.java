package tourGuide;


import java.util.*;
import java.util.concurrent.ThreadLocalRandom;



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
import tourGuide.model.ProviderModel;
import tourGuide.model.location.AttractionModel;
import tourGuide.model.location.LocationModel;
import tourGuide.model.location.VisitedLocationModel;
import tourGuide.repository.InternalTestService;
import tourGuide.service.*;
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
		InternalTestService internalTestService = new InternalTestService();
		UserService userService = new UserService(internalTestService);
		GpsService gpsService = new GpsService();
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		TripDealsService tripDealsService = new TripDealsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService, userService);
		InternalTestHelper.setInternalUserNumber(0);

		tourGuideService.tracker.stopTracking();

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocationModel visitedLocationModel = tourGuideService.getUserLocation(user);

		assertEquals(visitedLocationModel.userId, user.getUserId());
	}

	@Test
	public void addUser() throws UserNotFoundException {

		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService();
		UserService userService = new UserService(internalTestService);
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		TripDealsService tripDealsService = new TripDealsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService, userService);


		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userService.addUser(user);
		userService.addUser(user2);

		User retrivedUser = userService.getUserFromUserName(user.getUserName());
		User retrivedUser2 = userService.getUserFromUserName(user2.getUserName());

		tourGuideService.tracker.stopTracking();

		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}

	@Test
	public void getAllUsers() {

		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService();
		UserService userService = new UserService(internalTestService);
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		TripDealsService tripDealsService = new TripDealsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService, userService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userService.addUser(user);
		userService.addUser(user2);

		List<User> allUsers = userService.getAllUsers();

		tourGuideService.tracker.stopTracking();

		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}

	@Test
	public void trackUser(){

		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService();
		UserService userService = new UserService(internalTestService);
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		TripDealsService tripDealsService = new TripDealsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService, userService);

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

		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService();
		UserService userService = new UserService(internalTestService);
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		TripDealsService tripDealsService = new TripDealsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService, userService);


		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		// VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		LocationModel locationModel = new LocationModel(33.000000,-117.000000);
		Date date = new Date(System.currentTimeMillis());
		VisitedLocationModel visitedLocationModel =new VisitedLocationModel(user.getUserId(), locationModel, date);

		List<AttractionModel> attractionModels = tourGuideService.getNearByAttractions(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(5, attractionModels.size());
	}

	/**
	 * in this test the new user=user don't have any
	 */
	@Test
	public void getTripDeals() {
		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService();
		UserService userService = new UserService(internalTestService);
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		TripDealsService tripDealsService = new TripDealsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService, userService);


		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		UserPreferences userPreferencesTest = new UserPreferences();
		userPreferencesTest.setTripDuration(1);
		userPreferencesTest.setNumberOfAdults(1);
		userPreferencesTest.setNumberOfChildren(1);

		user.setUserPreferences(userPreferencesTest);

		List<ProviderModel> providers = tourGuideService.getTripDeals(user);
		logger.debug("in TripDealsTest for provider :"+providers.get(0).name+ "price is : "+providers.get(0).price);

		tourGuideService.tracker.stopTracking();

		assertEquals(5, providers.size());
	}

	@Test
	public void getAllCurrentUsers() {

		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService();
		UserService userService = new UserService(internalTestService);
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		TripDealsService tripDealsService = new TripDealsService();
		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService, userService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		double longitude = ThreadLocalRandom.current().nextDouble(-180.0, 180.0);
		double latitude = ThreadLocalRandom.current().nextDouble(-85.05112878, 85.05112878);
		double longitude2 = ThreadLocalRandom.current().nextDouble(-180.0, 180.0);
		double latitude2 = ThreadLocalRandom.current().nextDouble(-85.05112878, 85.05112878);

		VisitedLocationModel lastVisitedLocationModelUser = new VisitedLocationModel(UUID.randomUUID(),new LocationModel(latitude,longitude), new Date(System.currentTimeMillis()));
		VisitedLocationModel lastVisitedLocationModelUser2 = new VisitedLocationModel(UUID.randomUUID(),new LocationModel(latitude2,longitude2), new Date(System.currentTimeMillis()));
		user.addToVisitedLocations(lastVisitedLocationModelUser);
		user2.addToVisitedLocations(lastVisitedLocationModelUser2);

		List<User> allUser = new ArrayList<>();
		allUser.add(user);
		allUser.add(user2);

		List<Map<String, LocationModel>> getAllLastLocation = tourGuideService.getAllCurrentUserlastLocation(allUser);

		tourGuideService.tracker.stopTracking();

		assertTrue(getAllLastLocation.get(0).containsValue(lastVisitedLocationModelUser.locationModel));
		assertTrue(getAllLastLocation.get(1).containsValue(lastVisitedLocationModelUser2.locationModel));
	}


}

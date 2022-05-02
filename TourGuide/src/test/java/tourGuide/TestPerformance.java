package tourGuide;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.time.StopWatch;


import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.repository.InternalTestService;
import tourGuide.service.GpsService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.TripDealsService;
import tourGuide.model.User;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPerformance {
	Logger logger = LoggerFactory.getLogger(TestPerformance.class);

	/*
	 * A note on performance improvements:
	 *
	 *     The number of users generated for the high volume tests can be easily adjusted via this method:
	 *
	 *     		InternalTestHelper.setInternalUserNumber(100000);
	 *
	 *
	 *     These tests can be modified to suit new solutions, just as long as the performance metrics
	 *     at the end of the tests remains consistent.
	 *
	 *     These are performance metrics that we are trying to hit:
	 *
	 *     highVolumeTrackLocation: 100,000 users within 15 minutes:
	 *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
	 *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */

	@BeforeAll
	private static void setUp (){
		Locale.setDefault(Locale.US);
	}
	@Test
	public void highVolumeTrackLocation(){
		GpsUtil gpsUtil = new GpsUtil();

		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(gpsUtil);
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		TripDealsService tripDealsService = new TripDealsService();

		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService);
		// Users should be incremented up to 100,000, and test finishes within 15 minutes
		InternalTestHelper.setInternalUserNumber(10000);
		List<User> allUsers = tourGuideService.getAllUsers();
	    StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		logger.debug("list size: "+allUsers.size());
		int nbOfExecutedThread = 0;
		AtomicInteger i = new AtomicInteger();
		i.set(0);
		// simulate user being tracked, for each user runTrackUser iterate over all user.
		allUsers.forEach(u -> {
			tourGuideService.runTrackUser(u);
			 i.getAndIncrement();
		});
		ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) tourGuideService.getExecutorService();
		while(threadPoolExecutor.getActiveCount() >0) {
			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.debug("executed thread :" + i);


		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}


	@Test
	public void highVolumeGetRewards() {
		GpsUtil gpsUtil = new GpsUtil();

		InternalTestService internalTestService = new InternalTestService();
		GpsService gpsService = new GpsService(gpsUtil);
		RewardsService rewardsService = new RewardsService(gpsService, new RewardCentral());
		TripDealsService tripDealsService = new TripDealsService();

		TourGuideService tourGuideService = new TourGuideService(rewardsService, gpsService, internalTestService, tripDealsService);

		// Users should be incremented up to 100,000, and test finishes within 20 minutes
		InternalTestHelper.setInternalUserNumber(100);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
	    Attraction attraction = gpsUtil.getAttractions().get(0);
		List<User> allUsers = new ArrayList<>();
		allUsers = tourGuideService.getAllUsers();
		allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

	    allUsers.forEach(u -> rewardsService.calculateRewards(u));

		for(User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}
		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

}

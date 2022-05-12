package tourGuide;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import tourGuide.service.GpsService;
import tourGuide.service.RewardsService;

@Configuration
public class TourGuideModule {
	


	public GpsService getGpsService (){
		return new GpsService();
	}
	
	@Bean
	public RewardsService getRewardsService() {
		return new RewardsService(getGpsService(),getRewardCentral());
	}
	
	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentral();
	}
	
}

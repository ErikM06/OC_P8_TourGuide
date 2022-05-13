package tourGuide.model;

import tourGuide.model.location.AttractionModel;
import tourGuide.model.location.VisitedLocation;

public class UserReward {

	public final VisitedLocation visitedLocation;
	public final AttractionModel attractionModel;
	private int rewardPoints;
	public UserReward(VisitedLocation visitedLocation, AttractionModel attractionModel, int rewardPoints) {
		this.visitedLocation = visitedLocation;
		this.attractionModel = attractionModel;
		this.rewardPoints = rewardPoints;
	}
	
	public UserReward(VisitedLocation visitedLocation, AttractionModel attractionModel) {
		this.visitedLocation = visitedLocation;
		this.attractionModel = attractionModel;
	}

	public void setRewardPoints(int rewardPoints) {
		this.rewardPoints = rewardPoints;
	}
	
	public int getRewardPoints() {
		return rewardPoints;
	}
	
}

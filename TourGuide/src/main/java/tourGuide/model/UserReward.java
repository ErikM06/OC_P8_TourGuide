package tourGuide.model;

import tourGuide.model.location.AttractionModel;
import tourGuide.model.location.VisitedLocationModel;

public class UserReward {

	public final VisitedLocationModel visitedLocationModel;
	public final AttractionModel attractionModel;
	private int rewardPoints;
	public UserReward(VisitedLocationModel visitedLocationModel, AttractionModel attractionModel, int rewardPoints) {
		this.visitedLocationModel = visitedLocationModel;
		this.attractionModel = attractionModel;
		this.rewardPoints = rewardPoints;
	}
	
	public UserReward(VisitedLocationModel visitedLocationModel, AttractionModel attractionModel) {
		this.visitedLocationModel = visitedLocationModel;
		this.attractionModel = attractionModel;
	}

	public void setRewardPoints(int rewardPoints) {
		this.rewardPoints = rewardPoints;
	}
	
	public int getRewardPoints() {
		return rewardPoints;
	}
	
}

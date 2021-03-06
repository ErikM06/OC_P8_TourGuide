package tourGuide.model;

import tourGuide.model.location.VisitedLocationModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class User {
	private final UUID userId;
	private final String userName;
	private String phoneNumber;
	private String emailAddress;
	private Date latestLocationTimestamp;
	private List<VisitedLocationModel> visitedLocationModels = new ArrayList<>();
	private List<UserReward> userRewards = new ArrayList<>();
	private UserPreferences userPreferences = new UserPreferences();
	private List<ProviderModel> tripDeals = new ArrayList<>();
	public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
		this.userId = userId;
		this.userName = userName;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
	}
	
	public UUID getUserId() {
		return userId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public void setLatestLocationTimestamp(Date latestLocationTimestamp) {
		this.latestLocationTimestamp = latestLocationTimestamp;
	}
	
	public Date getLatestLocationTimestamp() {
		return latestLocationTimestamp;
	}
	
	public void addToVisitedLocations(VisitedLocationModel visitedLocationModel) {
		visitedLocationModels.add(visitedLocationModel);
	}
	
	public List<VisitedLocationModel> getVisitedLocations() {
		return visitedLocationModels;
	}
	
	public void clearVisitedLocations() {
		visitedLocationModels.clear();
	}
	
	public void addUserReward(UserReward userReward) {
		userRewards.add(userReward);
	}
	
	public List<UserReward> getUserRewards() {
		return userRewards;
	}
	
	public UserPreferences getUserPreferences() {
		return userPreferences;
	}
	
	public void setUserPreferences(UserPreferences userPreferences) {
		this.userPreferences = userPreferences;
	}

	public VisitedLocationModel getLastVisitedLocation() {
		return visitedLocationModels.get(visitedLocationModels.size() - 1);
	}
	
	public void setTripDeals(List<ProviderModel> tripDeals) {
		this.tripDeals = tripDeals;
	}
	
	public List<ProviderModel> getTripDeals() {
		return tripDeals;
	}

}

package tourGuide.repository;

import tourGuide.model.location.LocationModel;
import tourGuide.model.location.VisitedLocationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.customExceptions.UserAlreadyExistException;
import tourGuide.customExceptions.UserNotFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class InternalTestService {

    private final Logger logger = LoggerFactory.getLogger(InternalTestService.class);
    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    public static final String tripPricerApiKey = "test-server-api-key";
    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
    public final Map<String, User> internalUserMap = new HashMap<>();

    /**
     * initialize internals user mapped in Map<String, User> internalUserMap
     * i = user iteration
     */
    public void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);
            if (internalUserMap.containsKey(userName)){
                throw new UserAlreadyExistException("Already exist : "+userName);
            }
            internalUserMap.put(userName, user);
        });
        logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
    }

    public User getUser(String userName) throws UserNotFoundException {
        User internalUser = internalUserMap.get(userName);
        if (!internalUserMap.containsKey(userName)){
            throw new UserNotFoundException("User :"+userName+" not Found!");
        }
        return internalUser;
    }

    /**
     * generate a Location
     * @Param user
     */
    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i-> {
            user.addToVisitedLocations(new VisitedLocationModel(user.getUserId(), new LocationModel(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

}


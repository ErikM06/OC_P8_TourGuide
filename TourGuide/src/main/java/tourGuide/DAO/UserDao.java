package tourGuide.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.repository.InternalTestService;
import tourGuide.customExceptions.UserNotFoundException;
import tourGuide.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDao {

    Logger logger = LoggerFactory.getLogger(UserDao.class);

    private final InternalTestService internalTestService;

    public UserDao(InternalTestService internalTestService){
        this.internalTestService = internalTestService;
    }

    public User getUserFromUserName (String userName) throws UserNotFoundException {
        return internalTestService.getUser(userName);
    }
    public void initializeFromInternalTestService (){
        logger.info("TestMode enabled");
        logger.debug("Initializing users");
        this.internalTestService.initializeInternalUsers();
        logger.debug("Finished initializing users");
    }
    public List<User> getAllUsers() {
        return internalTestService.internalUserMap.values().stream().collect(Collectors.toList());
    }

    public void addUser(User user) {
        if (!internalTestService.internalUserMap.containsKey(user.getUserName())) {
            internalTestService.internalUserMap.put(user.getUserName(), user);
        }
    }
}

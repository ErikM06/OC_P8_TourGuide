package tourGuide.service;

import org.springframework.stereotype.Service;
import tourGuide.customExceptions.UserNotFoundException;
import tourGuide.model.User;
import tourGuide.repository.InternalTestService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final InternalTestService internalTestService;

    public UserService(InternalTestService internalTestService) {
        this.internalTestService = internalTestService;
    }

    private User getUser(String userName) throws UserNotFoundException {
        return getUserFromUserName(userName);
    }
    public User getUserByUUID (UUID userID) throws UserNotFoundException {
        User user = null;
        Optional<User> userOptional = getAllUsers().stream().filter(s -> s.getUserId().equals(userID)).findFirst();
        if (!userOptional.isPresent()){
            throw  new UserNotFoundException("user with UUID :"+userID+" not found!");
        }

       return userOptional.get();
    }


    public User getUserFromUserName (String userName) throws UserNotFoundException {
        return internalTestService.getUser(userName);
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

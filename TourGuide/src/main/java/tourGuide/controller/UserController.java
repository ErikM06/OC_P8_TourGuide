package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.DTO.UserPreferencesDTO;
import tourGuide.customExceptions.UserNotFoundException;
import tourGuide.repository.InternalTestService;
import tourGuide.service.TourGuideService;

@RestController
public class UserController {

    @Autowired
    InternalTestService internalTestService;

    @Autowired
    TourGuideService tourGuideService;

    @RequestMapping("/updateUserPreferences")
    public String updateUserPreferences (@RequestParam String userName, @RequestBody UserPreferencesDTO userPreferencesDTO) throws UserNotFoundException {
        if(internalTestService.getUser(userName) ==null) {
            throw new UserNotFoundException(userName);
        }
        return JsonStream.serialize(new UserPreferencesDTO(userName,
                tourGuideService.updateUserPreferences(userName, userPreferencesDTO)));
    }

}

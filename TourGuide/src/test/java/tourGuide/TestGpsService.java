package tourGuide;

import tourGuide.model.location.AttractionModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tourGuide.service.GpsService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TestGpsService {

    private final Logger logger = LoggerFactory.getLogger(TestGpsService.class);


    @Test
    public void testGetAllAttractionService (){
        GpsService gpsService = new GpsService();
        List<AttractionModel> attractionModelList = gpsService.getAttractionsService();
        assertTrue(attractionModelList.size()>0);
    }
}

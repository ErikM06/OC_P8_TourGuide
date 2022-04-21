package tourGuide.service.util;

import tourGuide.service.GpsService;
import tourGuide.user.User;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ThreadSafe
public class ThreadTrackUserLocation {

    public void trackUserLocationPool (User user){

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
       //     GpsService gpsService = new GpsService();

        });
    }


}

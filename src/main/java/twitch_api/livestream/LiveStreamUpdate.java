package twitch_api.livestream;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitch_api.Config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class is the controller, which updates all the LiveStreams in LiveStreamHolder
 */
public class LiveStreamUpdate {




    private LiveStreamHolder liveStreamHolder;
    private Config config;
    ScheduledExecutorService executor;
    private Boolean running;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public LiveStreamUpdate(Config config) {
        this.config = config;
        running = false;
        liveStreamHolder = new LiveStreamHolder(config);
    }

    /**
     * First, clearing all livestreamer, then add them again.
     */
    public void readLiveStreamer(){
        liveStreamHolder.clearStreamers();
        config.getLiveStreamer().forEach(name ->{
            if (!liveStreamHolder.hasStreamer(name.toString()))liveStreamHolder.addStreamer(name.toString());
        });

    }

    public void startUpdating(){
        if (running) return;
        logger.info("start updating");
        running = true;

        readLiveStreamer();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                liveStreamHolder.updateMessages(config.getLiveStreamChannelUpdateID());
            }
        };
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(r,0,1, TimeUnit.MINUTES);
    }

    public void stopUpdating(){
        if (!running) return;
        logger.info("Stop updating");
        liveStreamHolder.deleteAllMessages(config.getLiveStreamChannelUpdateID());
        running = false;
        executor.shutdownNow();
    }



}

package twitch_api.livestream;

import main.botcore.EmbedWithPicture;
import main.botcore.MessageHolder;
import main.botcore.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitch_api.Config;
import twitch_api.Formatter;
import twitch_api.TwitchApiEndpoints;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose of this class is to hold all StreamDatas of all live streamers
 */
public class LiveStreamHolder {

    List<MessageData> allStreams;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Config config;


    public LiveStreamHolder(Config config){
        allStreams = new ArrayList<>();
        this.config = config;
    }


    public void addStreamer(String streamerName){
        int id = TwitchApiEndpoints.getClientID(streamerName);
        if (id == 0) return;
        allStreams.add(new MessageData(streamerName,id));
    }


    /**
     * Updating all LiveStreamers, which are safed in the @link{allStreams List}.
     * @param channelID
     */
    public void updateMessages(long channelID){
        logger.debug("Checking live streams for channel " + channelID);
        allStreams.forEach(messageData -> {
            try {
                logger.debug("Checking messageData from " + messageData.getStreamerName());
                messageData.setStreamData(TwitchApiEndpoints.getLiveStreamByUser(messageData.getStreamerID()));
                messageData.getStreamData().setCustomMessage(config.getCustomMessage(messageData.getStreamerName()));

                if (messageData.isOnline()) {
                    if (messageData.getMessageID() != 0) {
                        EmbedWithPicture embed = editEmbedWithPicture(messageData.getStreamData(), messageData.getEmbedWithPicture());
                        Output.editEmbedMessageByID(messageData.getMessageID(), channelID, embed.getEmbedBuilder().build());

                    } else {
                        MessageHolder messageHolder = new MessageHolder();
                        messageData.setEmbedWithPicture(Formatter.buildEmbedWithPictureFromStreamData(messageData.getStreamData()));
                        Output.sendMessageToChannel(channelID,
                                messageData.getEmbedWithPicture(),
                                messageHolder);
                        messageData.setMessageID(messageHolder.getMessage().getIdLong());
                    }
                } else {
                    if (messageData.getMessageID() != 0) {
                        Output.deleteMessageByID(messageData.getMessageID(), channelID);
                        messageData.setMessageID(0);
                    }
                }
            } catch (Exception e){
                logger.warn("{} in updateMessage with {}",e.getMessage(),messageData.getStreamerName());
            }
        });

    }

    public void deleteAllMessages(long channelID){
        allStreams.forEach(messageData -> {
            if(messageData.getMessageID() !=0){
                Output.deleteMessageByID(messageData.getMessageID(),channelID);
                messageData.setMessageID(0L);
            }
        });
    }

    /**
     * Remove all Streamers.
     */
    public void clearStreamers(){
        allStreams = new ArrayList<>();
    }


    private EmbedWithPicture editEmbedWithPicture(StreamData streamData, EmbedWithPicture embed){
        return Formatter.updateEmbedWithPicture(streamData, embed);
    }

    protected boolean hasStreamer(String name){
        for (MessageData messageData : allStreams) {
            if (messageData.getStreamerName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

}

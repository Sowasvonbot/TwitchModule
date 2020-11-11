package twitch_api;


import main.botcore.EmbedWithPicture;
import main.botcore.MyEmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitch_api.livestream.StreamData;

import java.util.Random;

public class Formatter {

    private static final Logger logger = LoggerFactory.getLogger("Twitch Formatter");


    public static EmbedWithPicture buildEmbedWithPictureFromStreamData(StreamData data){
        String customMessage = data.getCustomMessage() == null ? "" : data.getCustomMessage();
         return new EmbedWithPicture(
                new MyEmbedBuilder()
                        //setDescription(data.toString() + " " + data.getStreamLink().toString())
                        .addField("Game", data.getGame(), true)
                        .addField("Title", data.getStreamName(), true)
                        .addField("Viewer", Integer.toString(data.getViewer()), true)
                        .setTitle(data.getStreamerName(), data.getStreamLink().toString())
                        .setDescription(customMessage)
                        .setThumbnail(data.getLogo().toString())
                        .setImage(data.getPictureURL().toString()),
                 null,
                 null);
    }

    public static EmbedWithPicture updateEmbedWithPicture(StreamData data, EmbedWithPicture embed){
        try {
            String customMessage = data.getCustomMessage() == null ? "" : data.getCustomMessage();
            int timeStamp = new Random().nextInt(100000);

            logger.debug("{}?{}", data.getPictureURL().toString(),timeStamp);
            embed.getEmbedBuilder().clearFields()
                    .addField("Game", data.getGame(), true)
                    .addField("Title", data.getStreamName(), true)
                    .addField("Viewer", Integer.toString(data.getViewer()), true)
                    .setDescription(customMessage)
                    .setThumbnail(data.getLogo().toString())
                    .setImage(data.getPictureURL().toString()+ "?" +  timeStamp);
        } catch (Exception e){
            e.printStackTrace();
        }
        return embed;

    }
}

package twitch_api.livestream;


import main.botcore.EmbedWithPicture;

import javax.annotation.Nullable;


public class MessageData {


    private String streamerName;
    private StreamData streamData;
    private long messageID;
    private int streamerID;
    private EmbedWithPicture embedWithPicture;

    public MessageData(String streamerName, int streamerID) {
        this.streamerName = streamerName;
        this.streamerID = streamerID;
        messageID = 0;
    }

    public void setEmbedWithPicture(EmbedWithPicture embedWithPicture) {
        this.embedWithPicture = embedWithPicture;
    }

    public EmbedWithPicture getEmbedWithPicture() {
        return embedWithPicture;
    }

    public void setStreamData(StreamData streamData) {
        this.streamData = streamData;
    }

    public void setMessageID(long messageID) {
        this.messageID = messageID;
    }

    public int getStreamerID() {
        return streamerID;
    }

    public String getStreamerName() {
        return streamerName;
    }

    public StreamData getStreamData() {
        return streamData;
    }

    public long getMessageID() {
        return messageID;
    }

    public boolean isOnline(){
        if (streamData == null) return false;
        return streamData.isOnline();
    }
}

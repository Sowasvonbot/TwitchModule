package twitch_api;

import main.botcore.Bot;
import main.fileManagement.FileLoader;
import main.fileManagement.FileStringReader;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitch_api.livestream.StreamData;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class TwitchApiEndpoints {

    private static int active;
    private final static Logger logger = LoggerFactory.getLogger("TwitchApiEndpoints");

    public static void init(){
        try {
            active = 1;
            FileLoader.getInstance().loadFileFromClasspath("data/twitchClientId.txt");
        } catch (IOException e) {
            active = -1;
            logger.error("Can't load Twitch token");
        }
    }

    public static int getClientID(String userName){
        try {
            logger.info("Trying to get ClientID for {}", userName);
            Request request = templateBuilder()
                    .url("https://api.twitch.tv/kraken/users?login=" + userName)
                    .build();
            JSONObject answer = sendRequest(request);
            return answer.getJSONArray("users").getJSONObject(0).getInt("_id");
        } catch (JSONException e){
            logger.info(e.getMessage());
            return 0;
        }

    }

    public static StreamData getLiveStreamByUser(int userID){
        Request request = templateBuilder()
                .url("https://api.twitch.tv/kraken/streams/"+ userID)
                .build();
        JSONObject answer = sendRequest(request);
        //logger.info(answer.toString());
        return getStreamDataFromMessage(answer);
    }

    private static StreamData getStreamDataFromMessage(JSONObject message){
        if (message.get("stream").equals(null)) return new StreamData(false);
        StreamData streamData = new StreamData(true);
        try {
            JSONObject stream = message.getJSONObject("stream");
            JSONObject channel = stream.getJSONObject("channel");

            streamData = new StreamData(
                    true,
                    new URL(stream.getJSONObject("preview").getString("large")),
                    stream.getString("game"),
                    new URL(channel.getString("logo")),
                    channel.getString("name"),
                    channel.getString("status"),
                    new URL(channel.getString("url")),
                    stream.getInt("viewers")
                    );
        } catch (JSONException json){json.printStackTrace();}
        catch (MalformedURLException malformed){
            malformed.printStackTrace();
        }

        return streamData;
    }


    private static Request.Builder templateBuilder(){
        if (active == 0) init();
        if (active == -1) {
            logger.error("can't send request, twitch module is offline");
            return null;
        }
        return new Request.Builder()
                .addHeader("Client-ID", getTwitchClientId())
                .addHeader("Accept", "application/vnd.twitchtv.v5+json");
    }

    private static String getTwitchClientId(){
        return FileStringReader.getInstance().getFileContentAsString("twitchClientId");
    }

    private static JSONObject sendRequest(Request request){
        try {
            Response response = Bot.getHTTPClient().newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}

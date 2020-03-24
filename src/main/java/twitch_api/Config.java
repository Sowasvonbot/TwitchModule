package twitch_api;

import botcore.Bot;
import core.guild.modules.MiscModuleData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config implements MiscModuleData {

    private List<Object> liveStreamer;
    private long liveStreamChannelUpdateID;
    private boolean liveStreamWatch;
    private long liveStreamRoleID;

    private CommandController commandController;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public void setCommandController(CommandController commandController) {
        this.commandController = commandController;
    }

    @Override
    public boolean loadConfig(String fileContent) {
        try{
            JSONObject configData = new JSONObject(fileContent);

            liveStreamer = new ArrayList<>();
            JSONArray streamer = configData.getJSONArray("Streamer");
            liveStreamer = streamer.toList();
            liveStreamChannelUpdateID  = configData.getLong("liveStreamChannelUpdateID");
            liveStreamWatch = configData.getBoolean("liveStreamWatch");
            liveStreamRoleID = configData.getLong("liveStreamRoleID");
            if (this.commandController!=null) commandController.update();
            return true;
        } catch (JSONException e){
            logger.error("Couldn't load config data from {}", fileContent);
            logger.error("Loading init values");
            initDefaultValues();
            return false;
        }
    }

    @Override
    public String saveConfig() {
        JSONObject configData = new JSONObject();
        configData.put("Streamer", liveStreamer);
        configData.put("liveStreamChannelUpdateID", Long.toString(liveStreamChannelUpdateID));
        configData.put("liveStreamWatch", Boolean.toString(liveStreamWatch));
        configData.put("liveStreamRoleID", Long.toString(liveStreamRoleID));

        return  configData.toString();
    }


    private void initDefaultValues(){
        liveStreamer = new ArrayList<>();
        liveStreamChannelUpdateID = 0;
        liveStreamWatch = false;
        liveStreamRoleID = 0;
    }





    protected HashMap<String, String> getConfigVariables() {
        HashMap<String, String> configs = new HashMap<>();
        configs.put("Streamer", liveStreamerToString());
        configs.put("Update Channel ID", Long.toString(liveStreamChannelUpdateID));
        configs.put("Is updating", Boolean.toString(liveStreamWatch));

        return configs;
    }

    protected String getConfigDescription(){
        return new StringBuilder()
                .append("SetUpdateChannel: sets the text channel, where the bot writes the updating live streams").append("\n")
                .append("Usage: setUpdateChannel ChannelName").append("\n")
                .append("\n")
                .append("addStreamer: adds a new Streamer").append("\n")
                .append("Usage: addStreamer streamerName").append("\n")
                .append("\n")
                .append("setStreamer: resets all streamers and fills in the given Streamer").append("\n")
                .append("Usage: setStreamer streamerName").append("\n")
                .append("\n")
                .append("removeStreamer: removes the given streamer").append("\n")
                .append("Usage: removeStreamer streamerName").append("\n")
                .append("\n")
                .append("setStreamerRole: loads all members of this role as streamer to watched. It will be display, if a name couldn't be found at twitch ").append("\n")
                .append("Usage: setStreamerRole roleName").append("\n")
                .append("\n")
                .append("activate: activates the live stream updater").append("\n")
                .append("Usage: activate updater").append("\n")
                .append("\n")
                .append("deactivate: deactivates the live stream updater")
                .append("Usage: deactivate updater").append("\n")
                .toString();
    }

    protected String setConfigVariable(String variable, String value, long guildID) {
        try{
            switch (variable.toLowerCase()){
                case "setupdatechannel":
                    liveStreamChannelUpdateID = Bot.getChannelIDByName(value,guildID);
                    if (liveStreamChannelUpdateID == 0) return "Channel: "+ value + " not found!";
                    else return "Set channel: " + value + " as update channel";
                case "addstreamer":
                    if(addStreamer(value)) return "Success";
                    else return "Error: " + value + " not found on twitch";
                case "setstreamer":
                    return "Not working atm";
                case "removestreamer":
                    return removeStreamer(value);

                case "setstreamerrole":
                    long id = getIDFromRole(value,guildID);
                    if (id== 0) return "Role "+ value + " not found";
                    liveStreamRoleID = id;
                    return readAllLiveStreamerFromRole(value) ;
                case "activate":
                    if (liveStreamChannelUpdateID == 0) return "No channel to write in given";
                    liveStreamWatch = true;
                    if (this.commandController!=null) commandController.update();
                    return "Activated live stream updater";
                case "deactivate":
                    liveStreamWatch = false;
                    if (this.commandController!=null) commandController.update();
                    return "Deactivated live stream updater";
                default:
                    return "couldn't find command: " + variable;
            }
        } catch (Exception e){
            return e.getMessage();
        }
    }

    private String removeStreamer(String name){
        if (liveStreamer.remove(name)) return "Success";
        else return  "Streamer " + name + " not found in saved Streamers";
    }

    private boolean addStreamer(String name){
        int id = TwitchApiEndpoints.getClientID(name);
        if (id == 0) return false;
        else this.liveStreamer.add(name);
        return true;
    }

    private String readAllLiveStreamerFromRole(String role){
        List<String> liveStreamer = Bot.getAllMembersWithRole(liveStreamRoleID);
        List<String> notFound = new ArrayList<>();
        StringBuilder result = new StringBuilder("Successfully added all members from the role " + role);
        liveStreamer.forEach(name->{
            if (!addStreamer(name))notFound.add(name);
        });
        if (notFound.isEmpty()) return result.toString();
        result = new StringBuilder("not found:\n");
        for (String name : notFound) {
            result.append(name).append("\n");
        }
        return result + "Please add them manually";
    }


    private String liveStreamerToString(){
        if(liveStreamer.size() != 0) {
            StringBuilder allLivestreamer = new StringBuilder();
            for (int i = 0; i < liveStreamer.size(); i++) {
                allLivestreamer.append(", ").append(liveStreamer.get(i));
            }
            return  allLivestreamer.substring(2);
        }
        return "NONE";
    }


    private long getIDFromRole(String role, long guildID){
        return Bot.getRoleIDforGuild(guildID,role);
    }


    public List<Object> getLiveStreamer() {
        return liveStreamer;
    }

    public long getLiveStreamChannelUpdateID() {
        return liveStreamChannelUpdateID;
    }

    public boolean isLiveStreamWatch() {
        return liveStreamWatch;
    }

    public long getLiveStreamRoleID() {
        return liveStreamRoleID;
    }

    public Logger getLogger() {
        return logger;
    }


}

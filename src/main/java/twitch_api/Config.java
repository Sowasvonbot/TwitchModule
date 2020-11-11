package twitch_api;


import main.botcore.Bot;
import main.core.guild.modules.MiscModuleData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Config implements MiscModuleData {

    private List<Object> liveStreamer;
    private long liveStreamChannelUpdateID;
    private boolean liveStreamWatch;
    private long liveStreamRoleID;

    private Map<String,String> customMessages;

    private CommandController commandController;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void setCommandController(CommandController commandController) {
        this.commandController = commandController;
    }

    @Override
    public boolean loadConfig(String fileContent) {
        customMessages = new HashMap<>();
        liveStreamer = new ArrayList<>();
        try{
            JSONObject configData = new JSONObject(fileContent);


            JSONArray streamer = configData.getJSONArray("Streamer");
            liveStreamer = streamer.toList();
            liveStreamChannelUpdateID  = configData.getLong("liveStreamChannelUpdateID");
            liveStreamWatch = configData.getBoolean("liveStreamWatch");
            liveStreamRoleID = configData.getLong("liveStreamRoleID");
            if (this.commandController!=null) commandController.update();

            try{
                this.customMessages = getCustomMessagesAsMapFromJSONObject(configData.getJSONObject("customMessages"));
            } catch (JSONException e){
                customMessages = new HashMap<>();
                logger.warn("No customMessages Object found");
            }

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
        configData.put("customMessages", (customMessages));

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
        return ConfigFormatter.formatConfig(this);
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

                case "setcustommessage":
                    return setCustomMessage(value);
                default:
                    return "couldn't find command: " + variable;
            }
        } catch (Exception e){
            return e.getMessage();
        }
    }

    private String setCustomMessage(String value){
        String[] valueArray = value.split(":");
        if(valueArray.length != 2) return "Format not valid";

        String streamerName = valueArray[0];
        String customMessage = valueArray[1];

        if(!liveStreamer.contains(streamerName)) return "Streamer: **" + streamerName + "** not found";

        if(customMessages.containsKey(streamerName)) this.changeCustomMessageByStreamer(streamerName,customMessage);
        else this.addCustomMessage(streamerName,customMessage);
        return ConfigFormatter.appendCustomMessages(new StringBuilder(),this).toString();
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

    private Map<String,String> getCustomMessagesAsMapFromJSONObject(JSONObject jsonArray){

        Map<String,String> result = new HashMap<>();

        for (Iterator<String> i = jsonArray.keys();i.hasNext();) {
            try{
                String streamerName = i.next();
                String customMessage = jsonArray.getString(streamerName);
                result.put(streamerName,customMessage);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        return result;
    }

    /*private JSONObject MapToJSONObject(Map map){
        JSONObject result = new JSONObject()
    }*/


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


    public void addCustomMessage(String streamer, String message) {
        customMessages.put(streamer,message);

    }

    public void changeCustomMessageByStreamer(String streamer, String message) {
        if(customMessages.containsKey(streamer)) customMessages.replace(streamer,message);
    }

    public String getCustomMessage(String streamer) {
        return  customMessages.get(streamer) == null ? "":customMessages.get(streamer);
    }
}

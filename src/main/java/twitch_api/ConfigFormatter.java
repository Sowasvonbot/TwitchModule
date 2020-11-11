package twitch_api;

public abstract class ConfigFormatter {



    public static String formatConfig(Config config){
        StringBuilder res = new StringBuilder();
        appendUsage(res);
        res.append("\n\n");
        appendStreamer(res,config);
        res.append("\n\n");
        appendCustomMessages(res,config);

        return res.toString();

    }



    protected static StringBuilder appendUsage(StringBuilder sb){
        sb.append("===========================================================")
                .append("\n")
                .append("SetUpdateChannel: sets the text channel, where the bot writes the updating live streams").append("\n")
                .append("Usage: **setUpdateChannel ChannelName**").append("\n")
                .append("\n")
                .append("addStreamer: adds a new Streamer").append("\n")
                .append("Usage: **addStreamer streamerName**").append("\n")
                .append("\n")
                .append("setStreamer: resets all streamers and fills in the given Streamer").append("\n")
                .append("Usage: **setStreamer streamerName**").append("\n")
                .append("\n")
                .append("removeStreamer: removes the given streamer").append("\n")
                .append("Usage: **removeStreamer streamerName**").append("\n")
                .append("\n")
                .append("setStreamerRole: loads all members of this role as streamer to watched. It will be display, if a name couldn't be found at twitch ").append("\n")
                .append("Usage: **setStreamerRole roleName**").append("\n")
                .append("\n")
                .append("activate: activates the live stream updater").append("\n")
                .append("Usage: **activate updater**").append("\n")
                .append("\n")
                .append("deactivate: deactivates the live stream updater").append("\n")
                .append("Usage: **deactivate updater**").append("\n")
                .append("\n")
                .append("setCustomMessage: Set a custom message for given streamer").append("\n")
                .append("Usage: **setCustomMessage StreamerName:Here your message**").append("\n")
                .append("To delete a message, simply type setCustomMessage StreamerName:none").append("\n")
                .append("\n")
                .append("===========================================================");
        return sb;
    }

    protected static StringBuilder appendStreamer(StringBuilder sb, Config config){
        if(config.getLiveStreamer() == null ) return sb;
        sb.append("===========================================================").append("\n")
                .append("Current Livestreamer to watch:").append("\n\n");

        config.getLiveStreamer().forEach(streamer -> {
            sb.append(streamer.toString()).append(",");
        });
        sb.deleteCharAt(sb.length()-1);
        sb.append("\n");
        sb.append("===========================================================");
        return sb;
    }

    protected static StringBuilder appendCustomMessages(StringBuilder sb, Config config){

        sb.append("===========================================================").append("\n")
                .append("Current CustomMessages").append("\n\n");
        config.getLiveStreamer().forEach(streamer ->{
            sb.append("**").append(streamer.toString()).append("**").append(": ").append(config.getCustomMessage(streamer.toString())).append("\n");
        });

        sb.append("===========================================================");

        return sb;
    }


}

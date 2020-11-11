package twitch_api;

import main.botcore.MessageHolder;
import main.botcore.MyMessageBuilder;
import main.core.guild.modules.CommandReturn;
import main.core.guild.modules.commands.Command;
import twitch_api.livestream.LiveStreamUpdate;
import twitch_api.livestream.StreamData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandController implements main.core.guild.modules.CommandController {

    private Config config;
    private List<MessageHolder> messageHolders;
    private LiveStreamUpdate updater;

    public CommandController(Config config) {
        this.config = config;
        updater = new LiveStreamUpdate(config);
        messageHolders = new ArrayList<>();
        config.setCommandController(this);
    }

    @Override
    public String getName() {
        return "Twitch";
    }

    @Override
    public List<Command> getCommands() {
        Command command = new Command("getLiveStream",
                null,
                Command.returnValues.STRING,
                "tries to find the given streamer and returns the livestream");
        Command command2 = new Command("ping", null, Command.returnValues.STRING, "writes Pong :D");
        List<Command> list = new ArrayList<>();
        list.add(command);
        list.add(command2);
        return list;
    }

    @Override
    public CommandReturn executeCommand(@Nonnull String command, String[] args) {
        CommandReturn commandReturn = null;
        switch (command.toLowerCase()) {
            case "ping":
                commandReturn = new CommandReturn(new MyMessageBuilder().append("pong").build());
                break;
            case "getlivestream":

                if (args != null && args.length >= 1) {
                    int clientId = TwitchApiEndpoints.getClientID(args[0]);
                    StreamData data = TwitchApiEndpoints.getLiveStreamByUser(clientId);
                    if (data.isOnline()) commandReturn = new CommandReturn(Formatter.buildEmbedWithPictureFromStreamData(data));
                    else commandReturn = new CommandReturn(data.toString());

                } else {
                    commandReturn = new CommandReturn("please use getLiveStream userHere");
                }
                break;
            case "getids":
                StringBuilder res = new StringBuilder("AllMessages: \n");
                for (MessageHolder messageHolder : messageHolders) {
                    res.append(messageHolder.toString()).append("\n");
                }
                commandReturn = new CommandReturn(res.toString());
                break;

        }
        MessageHolder messageHolder = new MessageHolder();
        messageHolders.add(messageHolder);
        return commandReturn.setMessageHolder(messageHolder);
    }

    @Override
    public String getConfigDescription() {
        return config.getConfigDescription();
    }

    @Override
    public HashMap<String, String> getConfigVariables() {
        return config.getConfigVariables();
    }

    @Override
    public String setConfigVariable(String variable, String value, long guildID) {
        String res =  config.setConfigVariable(variable,value, guildID);
        this.update();
        return res;
    }


    protected void update(){
        this.updater.stopUpdating();
        if (config.isLiveStreamWatch()) this.updater.startUpdating();
    }
}

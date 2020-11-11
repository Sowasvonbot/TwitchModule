
import main.core.BigDiscordBot;
import twitch_api.ModuleAPI;

public class Main {


    public static void main(String[] args) {
        BigDiscordBot.getInstance().registerModule(ModuleAPI.class);
        BigDiscordBot.getInstance().startBot();
    }
}

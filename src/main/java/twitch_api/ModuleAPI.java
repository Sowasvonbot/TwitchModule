package twitch_api;

public class ModuleAPI extends core.guild.modules.ModuleAPI {




    public ModuleAPI() {
        super();
        Config config = new Config();
        this.setMiscModuleData(config);
        this.setCommandController(new CommandController(config));
        this.setName("Twitch");
    }

}

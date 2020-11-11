package twitch_api;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigTest {

    Config defaultConfig;

    @Before
    public void initDefaultConfig(){
        defaultConfig = new Config();
        defaultConfig.loadConfig("");
    }

    @Test
    public void testInitValues(){

        assertEquals(0L, defaultConfig.getLiveStreamChannelUpdateID());
        assertEquals(0L, defaultConfig.getLiveStreamRoleID());
        assertFalse(defaultConfig.isLiveStreamWatch());


        defaultConfig.addCustomMessage("testStreamer","TestMyMessage");

        assertEquals("TestMyMessage", defaultConfig.getCustomMessage("testStreamer"));

        defaultConfig.addCustomMessage("testStreamer2","drölf");

        defaultConfig.changeCustomMessageByStreamer("testStreamer2","Hallo Welt!");

        assertEquals("Hallo Welt!", defaultConfig.getCustomMessage("testStreamer2"));

        defaultConfig.setConfigVariable("activate",null,1L);

        String filecontent = defaultConfig.saveConfig();

        defaultConfig = new Config();
        defaultConfig.loadConfig(filecontent);

        assertEquals("Hallo Welt!", defaultConfig.getCustomMessage("testStreamer2"));


    }

    @Test
    public void testConfigMessage(){
        System.out.println(ConfigFormatter.formatConfig(defaultConfig));
    }

}
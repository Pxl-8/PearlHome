package network.pxl8.pearlhome.config;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import network.pxl8.pearlhome.lib.LibMeta;

@Config(modid = LibMeta.MOD_ID, name = LibMeta.MOD_ID)
public class Conf {
    public static TeleportConf tp_config = new TeleportConf();
    public static MiscConf misc_config = new MiscConf();

    public static class TeleportConf {
        @Config.Comment({"Allow players to teleport home by throwing a pearl down.", "This is the main function of the mod but you can disable it I guess"})
        public Boolean ENABLE_PEARL_TELEPORT = true;
        @Config.Comment({"Require players to be grounded to teleport home"})
        public Boolean REQUIRE_GROUNDED = true;
        @Config.Comment({"Require players to be on land to teleport home"})
        public Boolean REQUIRE_NOT_IN_WATER = true;
        @Config.Comment({"Require players to be on land to teleport home"})
        public Boolean REQUIRE_NOT_IN_LAVA = true;
        @Config.Comment({"Require players to be not on fire to teleport home"})
        public Boolean REQUIRE_NOT_BURNING = true;
        @Config.Comment({"Require players to be sneaking to teleport home"})
        public Boolean REQUIRE_SNEAKING = false;

        @Config.Comment({"Will only teleport if location is safe and has space", "This can fail and no warning popups will show"})
        public Boolean SAFE_TELEPORT = false;
        @Config.Comment({"Will force the player to spawn on the top most block of the world spawn point", "Can disable this if you know your spawn point is safe, otherwise players may spawn underground"})
        public Boolean SPAWN_ON_SURFACE = true;
    }

    public static class MiscConf {
        @Config.Comment({"Enable actionbar messages"})
        public Boolean ENABLE_TIPS = true;
        @Config.Comment({"Will send localised tips in the players language", "REQUIRES the mod to be installed client side and lang files in the correct language to exist"})
        public Boolean ENABLE_LOCALISED_TIPS = false;
        @Config.Comment({"Will force set the players spawn point whenever they click on a bed, regardless if its day or night", "This will permanently set spawn and breaking the bed will not reset it"})
        public Boolean FORCE_SET_HOME = false;
        @Config.Comment({"Sends a message tip with the coords of the set spawnpoint, REQUIRES client to have the mod", "Otherwise will just send a generic set spawn point message"})
        public Boolean FORCE_SET_HOME_TIP_INFO = false;
        @Config.Comment({"Allows players to open their ender chest from anywhere on sneak-right-clicking an eye of ender"})
        public Boolean PORTABLE_ENDER_CHEST = false;
    }

    @Mod.EventBusSubscriber
    public static class ConfigSync {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event){
            if (event.getModID().equals(LibMeta.MOD_ID)) { ConfigManager.sync(LibMeta.MOD_ID, Config.Type.INSTANCE); }
        }
    }
}

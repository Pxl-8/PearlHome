package network.pxl8.pearlhome.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class Configuration {
    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.BooleanValue ENABLE_PEARL_TELEPORT;
    public static ForgeConfigSpec.BooleanValue REQUIRE_GROUNDED;
    public static ForgeConfigSpec.BooleanValue REQUIRE_NOT_IN_WATER;
    public static ForgeConfigSpec.BooleanValue REQUIRE_NOT_IN_LAVA;
    public static ForgeConfigSpec.BooleanValue REQUIRE_NOT_BURNING;
    public static ForgeConfigSpec.BooleanValue REQUIRE_SNEAKING;
    public static ForgeConfigSpec.BooleanValue SAFE_TELEPORT;
    public static ForgeConfigSpec.BooleanValue SPAWN_ON_SURFACE;
    public static ForgeConfigSpec.BooleanValue TELEPORT_SPAWN_ON_SNEAK;
    public static ForgeConfigSpec.BooleanValue TELEPORT_SPAWN_IN_WATER;

    public static ForgeConfigSpec.BooleanValue ENABLE_TIPS;
    public static ForgeConfigSpec.BooleanValue LOCALISATION;
    public static ForgeConfigSpec.BooleanValue FORCE_SET_HOME;
    public static ForgeConfigSpec.BooleanValue EXTRA_INFO;
    public static ForgeConfigSpec.BooleanValue PORTABLE_ENDER_CHEST;
    public static ForgeConfigSpec.BooleanValue USE_NETHER_STAR;

    static {
        COMMON_BUILDER.push("tp_config");
        setupTeleportConfig();
        COMMON_BUILDER.push("misc_config");
        setupMiscConfig();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    private static void setupTeleportConfig() {
        ENABLE_PEARL_TELEPORT =     COMMON_BUILDER.comment("Allow players to teleport home by throwing a pearl down.", "This is the main function of the mod but you can disable it I guess")
                .define("ENABLE_PEARL_TELEPORT", true);

        COMMON_BUILDER.push("require");
        REQUIRE_GROUNDED =          COMMON_BUILDER.comment("Require players to be grounded to teleport home")
                .define("REQUIRE_GROUNDED", true);
        REQUIRE_NOT_IN_WATER =      COMMON_BUILDER.comment("Require players to be on land to teleport home")
                .define("REQUIRE_NOT_IN_WATER", true);
        REQUIRE_NOT_IN_LAVA =       COMMON_BUILDER.comment("Require players to be on land to teleport home")
                .define("REQUIRE_NOT_IN_LAVA", true);
        REQUIRE_NOT_BURNING =       COMMON_BUILDER.comment("Require players to be not on fire to teleport home")
                .define("REQUIRE_NOT_BURNING", true);
        REQUIRE_SNEAKING =          COMMON_BUILDER.comment("Require players to be sneaking to teleport home")
                .define("REQUIRE_SNEAKING", false);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.push("teleport");
        //SAFE_TELEPORT =             COMMON_BUILDER.comment("Will only teleport if location is safe and has space", "This can fail and no warning popups will show")
        //        .define("SAFE_TELEPORT", false);
        SPAWN_ON_SURFACE =          COMMON_BUILDER.comment("Will force the player to spawn on the top most solid non leaf block of the world spawn point", "Can disable this if you know your spawn point is safe, otherwise players may spawn underground")
                .define("SPAWN_ON_SURFACE", true);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.push("spawn");
        TELEPORT_SPAWN_ON_SNEAK =   COMMON_BUILDER.comment("Will force teleport to go to world spawn instead of bed, useful if you have a spawn town or trading post", "Incompatible with REQUIRE_SNEAKING config")
                .define("TELEPORT_SPAWN_ON_SNEAK", false);
        TELEPORT_SPAWN_IN_WATER =   COMMON_BUILDER.comment("Will force teleport to go to world spawn instead of bed, useful if you have a spawn town or trading post", "Incompatible with REQUIRE_NOT_IN_WATER config")
                .define("TELEPORT_SPAWN_IN_WATER", false);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.pop();
    }

    private static void setupMiscConfig() {
        COMMON_BUILDER.push("tips");
        ENABLE_TIPS =               COMMON_BUILDER.comment("Enable actionbar messages")
                .define("ENABLE_TIPS", true);
        LOCALISATION =     COMMON_BUILDER.comment("Will send localised messages in the players language", "REQUIRES lang files in the correct language to exist on the client")
                .define("LOCALISATION", false);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.push("set_home");
        FORCE_SET_HOME =            COMMON_BUILDER.comment("Will force set the players spawn point whenever they click on a bed if it is day time, if it is night beds will function as normal")
                .define("FORCE_SET_HOME", true);
        EXTRA_INFO =   COMMON_BUILDER.comment("Sends a message tip with the coords of the set spawnpoint", "Otherwise will just send \"Spawn point set\"")
                .define("EXTRA_INFO", false);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.push("enderchest");
        PORTABLE_ENDER_CHEST =      COMMON_BUILDER.comment("Allows players to open their ender chest from anywhere on sneak-right-clicking an eye of ender")
                .define("PORTABLE_ENDER_CHEST", false);
        USE_NETHER_STAR =      COMMON_BUILDER.comment("Use a nether star instead if you feel like an eye of ender is too cheap")
                .define("USE_NETHER_STAR", false);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.pop();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }
}

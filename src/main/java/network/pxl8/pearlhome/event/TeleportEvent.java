package network.pxl8.pearlhome.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import network.pxl8.pearlhome.config.Configuration;

@Mod.EventBusSubscriber
public class TeleportEvent {

    @SubscribeEvent
    public static void interactItem(PlayerInteractEvent.RightClickItem event) {
        if(event.getSide().isClient() || !(event.getEntity() instanceof PlayerEntity)) { return; }

        PlayerEntity player = event.getPlayer();
        Item item = event.getItemStack().getItem();

        if(item.equals(Configuration.USE_NETHER_STAR.get() ? Items.NETHER_STAR : Items.ENDER_EYE) && player.isCrouching() && Configuration.PORTABLE_ENDER_CHEST.get()) {
            EnderChestInventory enderChest = player.getInventoryEnderChest();

            player.openContainer(new SimpleNamedContainerProvider((id, playerInv, playerEnt) ->
                    ChestContainer.createGeneric9X3(id, playerInv, enderChest),
                    (Configuration.LOCALISATION.get() ? new TranslationTextComponent("gui.pearlhome.portable_enderchest") : new StringTextComponent("Portable Ender Chest"))
            ));
            player.addStat(Stats.OPEN_ENDERCHEST);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void interactBlock(PlayerInteractEvent.RightClickBlock event) {
        if(event.getSide().isClient() || !(event.getEntity() instanceof PlayerEntity)) { return; }

        PlayerEntity player = event.getPlayer();
        BlockPos pos = event.getPos();

        World world = event.getWorld();

        DimensionType currentDim = world.getDimension().getType();
        DimensionType targetDim = player.getSpawnDimension();

        if(event.getItemStack().getItem().equals(Items.ENDER_PEARL) && player.getLookVec().y < -0.9) {
            if(!Configuration.ENABLE_PEARL_TELEPORT.get()) { return; }
            if(!player.onGround && Configuration.REQUIRE_GROUNDED.get()) {
                sendTip(player, TextFormatting.RED, "text.pearlhome.teleport.warn.grounded", "Cannot teleport while in the air!");
                return;
            }
            if(player.isInWater() && Configuration.REQUIRE_NOT_IN_WATER.get()) {
                sendTip(player, TextFormatting.RED, "text.pearlhome.teleport.warn.in_water", "Cannot teleport while in water!");
                return;
            }
            if(player.isInLava() && Configuration.REQUIRE_NOT_IN_LAVA.get()) {
                sendTip(player, TextFormatting.RED, "text.pearlhome.teleport.warn.in_lava", "Cannot teleport while in lava!");
                return;
            }
            if(player.isBurning() && Configuration.REQUIRE_NOT_BURNING.get()) {
                sendTip(player, TextFormatting.RED, "text.pearlhome.teleport.warn.burning", "Cannot teleport while on fire!");
                return;
            }
            if(!player.isCrouching() && Configuration.REQUIRE_SNEAKING.get()) {
                sendTip(player, TextFormatting.RED, "text.pearlhome.teleport.warn.sneaking", "Sneak to teleport home");
                return;
            }

            BlockPos home = null;

            try { home = player.getBedLocation(targetDim); }
            catch (NullPointerException e) {}

            if(player.isCrouching() && Configuration.TELEPORT_SPAWN_ON_SNEAK.get()) { home = null; }
            if(player.isInWater() && Configuration.TELEPORT_SPAWN_IN_WATER.get()) { home = null; }

            if(home != null) {
                if(currentDim != targetDim) { player.changeDimension(targetDim); }

                player.setPositionAndUpdate(home.getX() + 0.5, home.getY() + 1, home.getZ() + 0.5);
                player.addExperienceLevel(0);
                sendTip(player, TextFormatting.GREEN, "text.pearlhome.teleport.success.home", "Teleported Home!");
            } else {
                if(currentDim != targetDim) { player.changeDimension(targetDim); }

                World targetWorld = DimensionManager.getWorld(world.getServer(), targetDim, true, true);
                BlockPos spawn = (Configuration.SPAWN_ON_SURFACE.get() ? targetWorld.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, targetWorld.getSpawnPoint()) : targetWorld.getSpawnPoint());

                player.setPositionAndUpdate(spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5);
                player.addExperienceLevel(0);
                sendTip(player, TextFormatting.GREEN, "text.pearlhome.teleport.success.spawn", "Teleported to World Spawn!");
            }
        }

        if(world.getBlockState(pos).isIn(BlockTags.BEDS) && Configuration.FORCE_SET_HOME.get()) {
            if(world.getDayTime()%24000 < (world.isRaining() ? 12010 : 12542) || world.getDayTime()%24000 > (world.isRaining() ? 23992 : 23460)) {
                event.setUseBlock(Event.Result.DENY);
                player.setSpawnPoint(pos, true, true, currentDim);

                if(Configuration.EXTRA_INFO.get() && Configuration.ENABLE_TIPS.get() && Configuration.LOCALISATION.get()) {
                    player.sendStatusMessage(new TranslationTextComponent("text.pearlhome.misc.setspawn_info", pos.getX(), pos.getY(), pos.getZ())
                            .setStyle(new Style().setColor(TextFormatting.GREEN)), true);
                } else if(Configuration.EXTRA_INFO.get() && Configuration.ENABLE_TIPS.get()) {
                    player.sendStatusMessage(new StringTextComponent("Home Set! (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")")
                            .setStyle(new Style().setColor(TextFormatting.GREEN)), true);
                } else {
                    sendTip(player, TextFormatting.GREEN, "text.pearlhome.misc.setspawn", "Home Set!");
                }
            }
        }
    }

    private static void sendTip(PlayerEntity player, TextFormatting color, String key, String text) {
        if(Configuration.ENABLE_TIPS.get() && Configuration.LOCALISATION.get()) {
            player.sendStatusMessage(new TranslationTextComponent(key).setStyle(new Style().setColor(color)), true);
        } else if(Configuration.ENABLE_TIPS.get()) {
            player.sendStatusMessage(new StringTextComponent(text).setStyle(new Style().setColor(color)), true);
        }
    }

}

package network.pxl8.pearlhome.event;

import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import network.pxl8.pearlhome.config.Conf;
import network.pxl8.pearlhome.lib.PearlHomeTeleporter;

@Mod.EventBusSubscriber
public class TeleportEvent {

    @SubscribeEvent
    public static void interactItem(PlayerInteractEvent.RightClickItem event) {
        if(event.getSide().isClient() || !(event.getEntity() instanceof EntityPlayer)) { return; }

        EntityPlayer player = event.getEntityPlayer();
        Item item = event.getItemStack().getItem();

        if(item.equals(Items.ENDER_EYE) && player.isSneaking() && Conf.misc_config.PORTABLE_ENDER_CHEST) {
            InventoryEnderChest enderChest = player.getInventoryEnderChest();

            player.displayGUIChest(enderChest);
            player.addStat(StatList.ENDERCHEST_OPENED);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void interactBlock(PlayerInteractEvent.RightClickBlock event) {
        if(event.getSide().isClient() || !(event.getEntity() instanceof EntityPlayer)) { return; }

        EntityPlayer player = event.getEntityPlayer();
        BlockPos pos = event.getPos();

        int currentDim = player.getEntityWorld().provider.getDimension();
        int targetDim = player.getSpawnDimension();

        World world = event.getWorld();
        World targetWorld = DimensionManager.getWorld(targetDim);

        MinecraftServer server = world.getMinecraftServer();
        WorldServer worldServer = server.getWorld(currentDim);

        if(event.getItemStack().getItem().equals(Items.ENDER_PEARL) && player.getLookVec().y < -0.9) {
            if(!Conf.tp_config.ENABLE_PEARL_TELEPORT) { return; }
            if(!player.onGround && Conf.tp_config.REQUIRE_GROUNDED) {
                sendTip(player, TextFormatting.RED, "text.pearlhome.teleport.warn.grounded", "Cannot teleport while in the air!");
                return;
            }
            if(player.isInWater() && Conf.tp_config.REQUIRE_NOT_IN_WATER) {
                sendTip(player, TextFormatting.RED, "text.pearlhome.teleport.warn.in_water", "Cannot teleport while in water!");
                return;
            }
            if(player.isInLava() && Conf.tp_config.REQUIRE_NOT_IN_LAVA) {
                sendTip(player, TextFormatting.RED, "text.pearlhome.teleport.warn.in_lava", "Cannot teleport while in lava!");
                return;
            }
            if(player.isBurning() && Conf.tp_config.REQUIRE_NOT_BURNING) {
                sendTip(player, TextFormatting.RED, "text.pearlhome.teleport.warn.burning", "Cannot teleport while on fire!");
                return;
            }
            if(!player.isSneaking() && Conf.tp_config.REQUIRE_SNEAKING) {
                sendTip(player, TextFormatting.RED, "text.pearlhome.teleport.warn.sneaking", "Sneak to teleport home");
                return;
            }

            BlockPos home = null;
            try { home = EntityPlayer.getBedSpawnLocation(targetWorld, player.getBedLocation(targetDim), player.isSpawnForced(targetDim));
            } catch (NullPointerException e) {}

            if(home != null) {
                if(currentDim != targetDim) {
                    server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) player, targetDim, new PearlHomeTeleporter(worldServer, home.getX(), home.getY(), home.getZ()));
                }
                if(Conf.tp_config.SAFE_TELEPORT) {
                    player.attemptTeleport(home.getX(), home.getY(), home.getZ());
                } else {
                    player.setPositionAndUpdate(home.getX() + 0.5, home.getY() + 1, home.getZ() + 0.5);
                }
                player.addExperience(0);
                sendTip(player, TextFormatting.GREEN, "text.pearlhome.teleport.success.home", "Teleported Home!");
            } else {
                BlockPos spawn = (Conf.tp_config.SPAWN_ON_SURFACE ? targetWorld.getTopSolidOrLiquidBlock(targetWorld.getSpawnPoint()) : targetWorld.getSpawnPoint());
                if(currentDim != targetDim) {
                    server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) player, targetDim, new PearlHomeTeleporter(worldServer, spawn.getX(), spawn.getY(), spawn.getZ()));
                }
                if(Conf.tp_config.SAFE_TELEPORT) {
                    player.attemptTeleport(spawn.getX(), spawn.getY(), spawn.getZ());
                } else {
                    player.setPositionAndUpdate(spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5);
                }
                player.addExperience(0);
                sendTip(player, TextFormatting.GREEN, "text.pearlhome.teleport.success.spawn", "Teleported to world spawn!");
            }
        }

        if(world.getBlockState(pos).getBlock().equals(Blocks.BED) && Conf.misc_config.FORCE_SET_HOME) {
            IBlockState bedState = world.getBlockState(pos);
            EnumFacing bedFacing = Blocks.BED.getBedDirection(bedState, world, event.getPos());
            BlockPos safePos = getSafeExitLocation(world, pos, 0, bedFacing);
            player.setSpawnPoint(safePos, true);

            if(Conf.misc_config.FORCE_SET_HOME_TIP_INFO) {
                player.sendStatusMessage(new TextComponentTranslation("text.pearlhome.misc.setspawn_info", safePos.getX(), safePos.getY(), safePos.getZ())
                        .setStyle(new Style().setColor(TextFormatting.GREEN)), false);
            } else if(Conf.misc_config.ENABLE_TIPS && Conf.misc_config.ENABLE_LOCALISED_TIPS) {
                player.sendStatusMessage(new TextComponentTranslation("text.pearlhome.misc.setspawn")
                        .setStyle(new Style().setColor(TextFormatting.GREEN)), false);
            } else if(Conf.misc_config.ENABLE_TIPS) {
                player.sendStatusMessage(new TextComponentString("Spawn point set")
                        .setStyle(new Style().setColor(TextFormatting.GREEN)), false);
            }
        }

    }

    private static void sendTip(EntityPlayer player, TextFormatting color, String key, String text) {
        if(Conf.misc_config.ENABLE_TIPS && Conf.misc_config.ENABLE_LOCALISED_TIPS) {
            player.sendStatusMessage(new TextComponentTranslation(key).setStyle(new Style().setColor(color)), true);
        } else if(Conf.misc_config.ENABLE_TIPS) {
            player.sendStatusMessage(new TextComponentString(text).setStyle(new Style().setColor(color)), true);
        }
    }

    private static BlockPos getSafeExitLocation(World worldIn, BlockPos pos, int tries, EnumFacing enumfacing)
    {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        for (int l = 0; l <= 1; ++l)
        {
            int i1 = i - enumfacing.getFrontOffsetX() * l - 1;
            int j1 = k - enumfacing.getFrontOffsetZ() * l - 1;
            int k1 = i1 + 2;
            int l1 = j1 + 2;

            for (int i2 = i1; i2 <= k1; ++i2)
            {
                for (int j2 = j1; j2 <= l1; ++j2)
                {
                    BlockPos blockpos = new BlockPos(i2, j, j2);

                    if (hasRoomForPlayer(worldIn, blockpos))
                    {
                        if (tries <= 0)
                        {
                            return blockpos;
                        }

                        --tries;
                    }
                }
            }
        }

        return null;
    }

    private static boolean hasRoomForPlayer(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).isTopSolid() && !worldIn.getBlockState(pos).getMaterial().isSolid() && !worldIn.getBlockState(pos.up()).getMaterial().isSolid();
    }
}

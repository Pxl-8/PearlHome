package network.pxl8.pearlhome.lib;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ITeleporter;

public class PearlHomeTeleporter implements ITeleporter {
    private final WorldServer worldServer;

    private double x;
    private double y;
    private double z;

    public PearlHomeTeleporter(WorldServer worldServer, double x, double y, double z) {
        this.worldServer = worldServer;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void placeEntity(World world, Entity entity, float yaw) {
        worldServer.getChunkFromChunkCoords((int) Math.floor(this.x),(int) Math.floor(this.z));
        entity.setLocationAndAngles(x, y + 1.0F, z, entity.rotationYaw, 0.0F);
        entity.motionY = 0.0F;
        entity.motionX = 0.0F;
        entity.motionZ = 0.0F;
    }
}

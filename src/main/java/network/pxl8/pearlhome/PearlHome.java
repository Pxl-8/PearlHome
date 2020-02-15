package network.pxl8.pearlhome;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import network.pxl8.pearlhome.lib.LibMeta;
import network.pxl8.pearlhome.proxy.Proxy;

@Mod(modid = LibMeta.MOD_ID, version = LibMeta.VERSION)
public class PearlHome {
    @Mod.Instance
    public static PearlHome instance;

    @SidedProxy(clientSide = LibMeta.CLIENT_PROXY, serverSide = LibMeta.SERVER_PROXY, modId = LibMeta.MOD_ID)
    private static Proxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }
}

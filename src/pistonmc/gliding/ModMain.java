package pistonmc.gliding;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = ModMain.MODID, version = Tags_GENERATED.VERSION)
public class ModMain
{
    public static final String MODID = "gliding";
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (event.getSide().isClient()) {
            FMLCommonHandler.instance().bus().register(new ClientEvents());
        } else {
            FMLCommonHandler.instance().bus().register(new ServerEvents());
        }
        NETWORK.registerMessage(PacketFallCancel.Handler.class, PacketFallCancel.class, 0, Side.SERVER);
    }
}

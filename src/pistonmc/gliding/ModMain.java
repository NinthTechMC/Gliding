package pistonmc.gliding;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import pistonmc.gliding.core.Config;
import pistonmc.gliding.core.EnchantmentAcrobatic;
import pistonmc.gliding.core.EnchantmentGlider;

@Mod(modid = ModMain.MODID, version = Tags_GENERATED.VERSION)
public class ModMain
{
    public static final String MODID = "gliding";
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.init();
        try {
            Enchantment.addToBookList(new EnchantmentGlider(Config.enchantmentIdGlider));
            Enchantment.addToBookList(new EnchantmentAcrobatic(Config.enchantmentIdAcrobatic));
        } catch (Exception e) {
            throw new RuntimeException("Failed to register some enchantments. There might be an ID conflict. Fix in the config", e);
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (event.getSide().isClient()) {
            ClientEvents events = new ClientEvents();
            FMLCommonHandler.instance().bus().register(events);
            MinecraftForge.EVENT_BUS.register(events);
        }
        ServerEvents events = new ServerEvents();
        FMLCommonHandler.instance().bus().register(events);
        MinecraftForge.EVENT_BUS.register(events);
        NETWORK.registerMessage(PacketFallCancel.Handler.class, PacketFallCancel.class, 0, Side.SERVER);
    }
}

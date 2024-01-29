package pistonmc.gliding;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import pistonmc.gliding.core.GliderClient;

public class ClientEvents {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        EntityPlayer player = event.player;
        World world = player.worldObj;
        if (world == null || !world.isRemote) {
            return;
        }
        GliderClient.tickGliding(player);
    }
}

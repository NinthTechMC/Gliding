package pistonmc.gliding;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class ServerEvents {
    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        // TODO: server fall damage
    }
}

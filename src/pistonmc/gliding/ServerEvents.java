package pistonmc.gliding;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import pistonmc.gliding.core.EnchantmentAcrobatic;

public class ServerEvents {
    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.source != DamageSource.fall) {
            return;
        }
        if (!(event.entity instanceof EntityPlayer player)) {
            return;
        }

        if (EnchantmentAcrobatic.isOnPlayer(player)) {
            event.setCanceled(true);
        }
    }
}

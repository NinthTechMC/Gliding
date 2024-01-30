package pistonmc.gliding;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import pistonmc.gliding.core.EnchantmentAcrobatic;

public class ServerEvents {
    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        System.out.println("onLivingAttack");
        if (event.source != DamageSource.fall) {
            System.out.println("not fall");
            return;
        }
        if (!(event.entity instanceof EntityPlayer player)) {
            System.out.println("not player");
            return;
        }

        int l = player.inventory.armorInventory.length;
        for (int i = 0; i < l; i++) {
            ItemStack item = player.inventory.armorInventory[i];
            int acrobatic = EnchantmentHelper.getEnchantmentLevel(EnchantmentAcrobatic.instance.effectId, item);
            if (acrobatic == 0) {
                System.out.println("no acrobatic "+i);
                continue;
            }
            event.setCanceled(true);
            return;
        }
        System.out.println("no acrobatic");
    }
}

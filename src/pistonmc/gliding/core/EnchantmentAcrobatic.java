package pistonmc.gliding.core;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import pistonmc.gliding.ModMain;

public class EnchantmentAcrobatic extends EnchantmentAbstract {

    public static EnchantmentAcrobatic instance;

    public static boolean isOnPlayer(EntityPlayer player) {
        int l = player.inventory.armorInventory.length;
        for (int i = 0; i < l; i++) {
            ItemStack item = player.inventory.armorInventory[i];
            int acrobatic = EnchantmentHelper.getEnchantmentLevel(instance.effectId, item);
            if (acrobatic != 0) {
                return true;
            }
        }
        return false;
    }

    public EnchantmentAcrobatic(int id) {
        super(id, EnumEnchantmentType.armor);
        if (instance != null) {
            throw new RuntimeException("Registering enchantment twice");
        }
        instance = this;

        setName(ModMain.MODID + ".acrobatic");
    }

}

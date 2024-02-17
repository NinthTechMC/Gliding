package pistonmc.gliding.core;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import pistonmc.gliding.ModMain;

public class EnchantmentAerodynamic extends EnchantmentAbstract {
    public static int MAX_LEVEL = 2;

    public static EnchantmentAerodynamic instance;

    public static int getPlayerLevel(EntityPlayer player) {
        int l = player.inventory.armorInventory.length;
        int aerodynamic = 0;
        for (int i = 0; i < l; i++) {
            ItemStack item = player.inventory.armorInventory[i];
            aerodynamic += EnchantmentHelper.getEnchantmentLevel(instance.effectId, item);
        }
        return Math.max(0, Math.min(MAX_LEVEL*4, aerodynamic));
    }

    public EnchantmentAerodynamic(int id) {
        super(id, EnumEnchantmentType.armor);
        if (instance != null) {
            throw new RuntimeException("Registering enchantment twice");
        }
        instance = this;

        setName(ModMain.MODID + ".aerodynamic");
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

}

package pistonmc.gliding.core;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import pistonmc.gliding.ModMain;
import pistonmc.gliding.api.IGliderItem;

public class EnchantmentGlider extends EnchantmentAbstract {
    public static int MAX_LEVEL = 3;

    public static EnchantmentGlider instance;

    public static int getPlayerLevel(EntityPlayer player) {
        ItemStack item = player.inventory.armorInventory[2];
        if (item == null) {
            return 0;
        }
        int level = 0;
        if (item.getItem() instanceof IGliderItem gliderItem) {
            level += gliderItem.getGliderLevel(item);
            if (level >= MAX_LEVEL) {
                return MAX_LEVEL;
            }
        }
        level += EnchantmentHelper.getEnchantmentLevel(instance.effectId, item);
        return Math.max(0, Math.min(level, MAX_LEVEL));
    }

    public EnchantmentGlider(int id) {
        super(id, EnumEnchantmentType.armor_torso);
        if (instance != null) {
            throw new RuntimeException("Registering enchantment twice");
        }
        instance = this;
        setName(ModMain.MODID + ".glider");
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }
}

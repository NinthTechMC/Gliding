package pistonmc.gliding.core;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

public class EnchantmentAbstract extends Enchantment {

    protected EnchantmentAbstract(int id, EnumEnchantmentType type) {
        super(id, 1, type);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        if (!Config.enableEnchantTable) {
            return false;
        }
        return super.canApplyAtEnchantingTable(stack);
    }
}

package pistonmc.gliding.core;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;

public class EnchantmentGlider extends Enchantment {

    public static EnchantmentGlider instance;

    public EnchantmentGlider(int id) {
        super(id, 1, EnumEnchantmentType.armor_torso);
        if (instance != null) {
            throw new RuntimeException("Registering enchantment twice");
        }
        instance = this;
    }
}

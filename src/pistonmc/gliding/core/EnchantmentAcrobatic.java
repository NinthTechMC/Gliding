package pistonmc.gliding.core;

import net.minecraft.enchantment.EnumEnchantmentType;
import pistonmc.gliding.ModMain;

public class EnchantmentAcrobatic extends EnchantmentAbstract {

    public static EnchantmentAcrobatic instance;

    public EnchantmentAcrobatic(int id) {
        super(id, EnumEnchantmentType.armor);
        if (instance != null) {
            throw new RuntimeException("Registering enchantment twice");
        }
        instance = this;

        setName(ModMain.MODID + ".acrobatic");
    }

}

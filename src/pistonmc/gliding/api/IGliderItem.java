package pistonmc.gliding.api;

import net.minecraft.item.ItemStack;

/**
 * Interface for torso armor to implement to allow default glider enchantment level.
 */
public interface IGliderItem {
    int getGliderLevel(ItemStack stack);
}

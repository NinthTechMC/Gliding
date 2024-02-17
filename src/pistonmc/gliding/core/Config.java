package pistonmc.gliding.core;

import java.io.File;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import libpiston.config.ConfigCategoryContainer;
import libpiston.config.ConfigCategoryFactory;
import libpiston.config.ConfigFactory;
import libpiston.config.ConfigRoot;

public class Config {
    public static boolean enableEnchantTable;
    public static IntSet glideableDimensions;
    public static int enchantmentIdGlider;
    public static int enchantmentIdAerodynamic;
    public static int enchantmentIdAcrobatic;

    public static void init() {
        File file = new File("").toPath().resolve("config").resolve("Gliding.cfg").toFile();
        ConfigRoot root = new ConfigRoot(file);
        ConfigCategoryFactory categoryFactory = new ConfigCategoryFactory(root);

        {
            ConfigCategoryContainer category = categoryFactory.create("General", "General settings");
            ConfigFactory factory = new ConfigFactory(category);
            enableEnchantTable = factory.createBoolean(
                "EnableEnchantTable", 
                "Make so the enchantments in this mod can be obtained from the enchanting table", 
                true).get();
            int[] dimensions = factory.createIntegerList(
                "GlideableDimensions", 
                "List of dimensions where gliding is enabled", 
                new int[]{0, 1, -1}).get();
            glideableDimensions = new IntOpenHashSet();
            for (int dimension : dimensions) {
                glideableDimensions.add(dimension);
            }
        }

        {
            ConfigCategoryContainer category = categoryFactory.create("EnchantmentsID", "Enchantment IDs. Change if you have a conflict");
            ConfigFactory factory = new ConfigFactory(category);
            enchantmentIdGlider = factory.createInteger("Glider", "ID for the Glider enchantment", 120).get();
            enchantmentIdAerodynamic = factory.createInteger("Aerodynamic", "ID for the Aerodynamic enchantment", 121).get();
            enchantmentIdAcrobatic = factory.createInteger("Acrobatic", "ID for the Acrobatic enchantment", 122).get();
        }
    }
}

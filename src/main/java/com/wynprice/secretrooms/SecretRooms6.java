package com.wynprice.secretrooms;

import com.wynprice.secretrooms.server.data.SecretItemTagsProvider;
import com.wynprice.secretrooms.server.data.SecretRecipeProvider;
import com.wynprice.secretrooms.server.items.SecretItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SecretRooms6.MODID)
public class SecretRooms6 {
    public static final String MODID = "secretroomsmod";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public SecretRooms6() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::gatherData);
    }


    public static final ItemGroup TAB = new ItemGroup(-1, MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(SecretItems.CAMOUFLAGE_PASTE);
        }
    };

    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        if (event.includeServer()) {
            gen.addProvider(new SecretRecipeProvider(gen));
            gen.addProvider(new SecretItemTagsProvider(gen));
        }
    }
}

package net.warsnake.poppydelight;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.warsnake.poppydelight.blocks.ModBlocks;
import net.warsnake.poppydelight.client.render.BadTripRenderer;
import net.warsnake.poppydelight.client.render.CannabisRenderer;
import net.warsnake.poppydelight.client.render.OpiumRenderer;
import net.warsnake.poppydelight.client.render.ShroomsRenderer;
import net.warsnake.poppydelight.effect.ModEffects;
import net.warsnake.poppydelight.items.ModCreativeTabs;
import net.warsnake.poppydelight.items.ModItems;
import org.slf4j.Logger;

@Mod(PoppyDelight.MODID)
public class PoppyDelight {

    // please do not judge ts, i was prob high when I wrote half this code, aswell ive deleted and added like 15 diff dependencies in and out lol

    public static final String MODID = "poppydelight";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final OpiumRenderer OPIUM_EFFECT_RENDERER = new OpiumRenderer();
    public static final ShroomsRenderer SHROOM_EFFECT_RENDERER = new ShroomsRenderer();
    public static final BadTripRenderer BAD_SHROOM_EFFECT_RENDERER = new BadTripRenderer();
    public static final CannabisRenderer POT_EFFECT_RENDERER = new CannabisRenderer();

    public PoppyDelight(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModEffects.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
       // modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {}

    private void addCreative(net.minecraftforge.event.BuildCreativeModeTabContentsEvent event) {
       // ModCreativeTabs.fillTabs(event);
    }

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(MODID, path);
    }

    @Mod.EventBusSubscriber(modid = PoppyDelight.MODID,
            bus = Mod.EventBusSubscriber.Bus.MOD,
            value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }

    }

}

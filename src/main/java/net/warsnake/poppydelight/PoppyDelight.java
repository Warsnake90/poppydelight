package net.warsnake.poppydelight;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.warsnake.poppydelight.blocks.ModBlocks;
import net.warsnake.poppydelight.blocks.entity.ModBlockEntities;
import net.warsnake.poppydelight.client.render.*;
import net.warsnake.poppydelight.effect.DaturaEffect;
import net.warsnake.poppydelight.effect.ModEffects;
import net.warsnake.poppydelight.effect.TunnelVisionEffect;
import net.warsnake.poppydelight.items.ModCreativeTabs;
import net.warsnake.poppydelight.items.ModItems;
import net.warsnake.poppydelight.screen.DryingTableScreen;
import net.warsnake.poppydelight.screen.ModMenuTypes;
import net.warsnake.poppydelight.sounds.ModSounds;
import org.slf4j.Logger;

@Mod(PoppyDelight.MODID)
public class PoppyDelight {

    // please do not judge ts, i was prob high when I wrote half this code

    public static final String MODID = "poppydelight";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final OpiumRenderer OPIUM_EFFECT_RENDERER = new OpiumRenderer();
    public static final ShroomsRenderer SHROOM_EFFECT_RENDERER = new ShroomsRenderer();
    public static final BadTripRenderer BAD_SHROOM_EFFECT_RENDERER = new BadTripRenderer();
    public static final CannabisRenderer POT_EFFECT_RENDERER = new CannabisRenderer();
    public static final TunnelVisionRenderer TUNNEL_VISION_RENDERER = new TunnelVisionRenderer();
    public static final DaturaRenderer DATURA_RENDERER = new DaturaRenderer();

    public PoppyDelight(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModEffects.register(modEventBus);
        ModSounds.register(modEventBus);

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
            MenuScreens.register(ModMenuTypes.DRYINGTABLE_MENU.get(), DryingTableScreen::new);
            MinecraftForge.EVENT_BUS.register(new MessageGarblerEvent());
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.DRYINGTABLE_BE.get(), DryingRackBlockEntityRenderer::new);
        }
    }


}

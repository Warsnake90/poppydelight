package net.warsnake.poppydelight;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import net.warsnake.poppydelight.items.ModCreativeTabs;
import net.warsnake.poppydelight.items.ModItems;
//import net.warsnake.poppydelight.recipes.ModRecipes;
import org.slf4j.Logger;

@Mod(PoppyDelight.MODID)
public class PoppyDelight
{
    public static final String MODID = "poppydelight";
    private static final Logger LOGGER = LogUtils.getLogger();

    public PoppyDelight(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.register(this);
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
       // ModRecipes.RECIPE_SERIALIZERS.register(bus);
        bus.addListener(this::setup);

    }

    private void setup(final FMLCommonSetupEvent event) {
        // ModRecipes.registerRecipeTypes();
    }

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(MODID, path);
    }

    @SubscribeEvent
    public void register(final RegisterEvent event) {
        ModItems.register((path, item) -> {
            event.register(Registries.ITEM, resource(path), () -> item);
        });
        //Sounds.register((sound) -> {
        //   event.register(Registries.SOUND_EVENT, sound.getLocation(), () -> sound);
        //});
    }

    @SubscribeEvent
    public void registerTabs(RegisterEvent event) {
        event.register(Registries.CREATIVE_MODE_TAB, helper -> {
            ModCreativeTabs.register((key, tab) -> helper.register(key, tab));
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        ModCreativeTabs.addToCreativeTab(event.getTabKey(), item -> event.accept(item));
    }

   // @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }
    }
}

package net.warsnake.poppydelight;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import umpaz.brewinandchewin.common.fluid.AlcoholFluidType;
import umpaz.brewinandchewin.common.registry.BnCFluids;

public class ModFluids extends BnCFluids {

    public static final DeferredRegister<FluidType> FLUID_TYPES;
    public static final DeferredRegister<Fluid> FLUIDS;

   // public static final RegistryObject<FluidType> ABSINTHE_FLUID_TYPE;
   // public static final RegistryObject<FlowingFluid> ABSINTHE;
   // public static final RegistryObject<FlowingFluid> FLOWING_ABSINTHE;
  //  public static final ForgeFlowingFluid.Properties ABSINTHE_FLUID_PROPERTIES;

    static {

        FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, "poppydelight");
        FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, "poppydelight");

       // ABSINTHE_FLUID_TYPE = FLUID_TYPES.register("absinthe_type", () -> new AlcoholFluidType(-14296393));

       // ABSINTHE = FLUIDS.register("absinthe", () -> new ForgeFlowingFluid.Source(ABSINTHE_FLUID_PROPERTIES));
        // FLOWING_ABSINTHE = FLUIDS.register("flowing_absinthe", () -> new ForgeFlowingFluid.Flowing(ABSINTHE_FLUID_PROPERTIES));
       // ABSINTHE_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(ABSINTHE_FLUID_TYPE, ABSINTHE, FLOWING_ABSINTHE);

    }

}

package net.warsnake.poppydelight;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {

    public static final ResourceKey<DamageType> TOXIC_FOOD =
            ResourceKey.create(Registries.DAMAGE_TYPE,
                    new net.minecraft.resources.ResourceLocation("poppydelight", "toxic_food"));
}

package net.warsnake.poppydelight.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.warsnake.poppydelight.blocks.entity.DryingTableBlockEntity;
import net.warsnake.poppydelight.items.ModItems;

public class DryingRackBlockEntityRenderer implements BlockEntityRenderer<DryingTableBlockEntity> {

    public DryingRackBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) { }

    @Override
    public void render(DryingTableBlockEntity be, float partialTick, PoseStack pose,
                       MultiBufferSource buffers, int light, int overlay) {

        ItemStack priority = be.getPriorityStack();
        if (priority.isEmpty()) return;

        ResourceLocation modelRL = getModelForItem(priority);
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelRL);

        pose.pushPose();
        Minecraft.getInstance().getBlockRenderer()
                .getModelRenderer()
                .renderModel(pose.last(), buffers.getBuffer(RenderType.solid()),
                        null, model, 1, 1, 1, light, overlay);
        pose.popPose();
    }

    private ResourceLocation getModelForItem(ItemStack stack) {

         /*
         models will be added to this as they finish being made 
        if (stack.is(ModItems.CANNABISBUD.get()))
            return new ResourceLocation("modid", "block/dryingtable_wetcan");
        if (stack.is(ModItems.DRYHEMPLEAF.get()))
            return new ResourceLocation("modid", "block/dryingtable_drycan");

        if (stack.is(ModItems.SHROOMS1.get()))
            return new ResourceLocation("modid", "block/dryingtable_wetshroom");
        if (stack.is(ModItems.SHROOMS2.get()))
            return new ResourceLocation("modid", "block/dryingtable_wetshroom");

        if (stack.is(ModItems.DRYSHROOMS1.get()))
            return new ResourceLocation("modid", "block/dryingtable_dryshroom");

        if (stack.is(ModItems.DRYSHROOMS2.get()))
            return new ResourceLocation("modid", "block/dryingtable_dryshroom");

          */

        return new ResourceLocation("modid", "block/dryingtable");
    }
}

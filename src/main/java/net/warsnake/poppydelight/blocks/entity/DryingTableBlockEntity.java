package net.warsnake.poppydelight.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.warsnake.poppydelight.blocks.ModBlocks;
import net.warsnake.poppydelight.items.ModItems;
import net.warsnake.poppydelight.screen.DryingTableMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DryingTableBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(6);

    private static final int INPUT_SLOT_A = 0;
    private static final int OUTPUT_SLOT_A = 1;
    private static final int INPUT_SLOT_B = 2;
    private static final int OUTPUT_SLOT_B = 3;
    private static final int INPUT_SLOT_C = 4;
    private static final int OUTPUT_SLOT_C = 5;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;

    public DryingTableBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.DRYINGTABLE_BE.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex){
                    case 0 -> DryingTableBlockEntity.this.progress;
                    case 1 -> DryingTableBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex){
                    case 0 -> DryingTableBlockEntity.this.progress = pValue;
                    case 1 -> DryingTableBlockEntity.this.maxProgress = pValue;
                };
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.poppydelight.dryingtable");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new DryingTableMenu(pContainerId, pPlayerInventory, this, this.data);
    }
    // drying tables are common so use _pd at the end to avoid crashes
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("dryingtable_pd.progress", progress);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("dryingtable_pd.progress");
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        boolean anyRecipe = false;

        if (hasRecipe(INPUT_SLOT_A, OUTPUT_SLOT_A)) {
            anyRecipe = true;
        }
        if (hasRecipe(INPUT_SLOT_B, OUTPUT_SLOT_B)) {
            anyRecipe = true;
        }
        if (hasRecipe(INPUT_SLOT_C, OUTPUT_SLOT_C)) {
            anyRecipe = true;
        }

        if (anyRecipe) {
            progress++;
            setChanged(pLevel, pPos, pState);

            if (progress >= maxProgress) {
                if (hasRecipe(INPUT_SLOT_A, OUTPUT_SLOT_A)) craftItem(INPUT_SLOT_A, OUTPUT_SLOT_A);
                if (hasRecipe(INPUT_SLOT_B, OUTPUT_SLOT_B)) craftItem(INPUT_SLOT_B, OUTPUT_SLOT_B);
                if (hasRecipe(INPUT_SLOT_C, OUTPUT_SLOT_C)) craftItem(INPUT_SLOT_C, OUTPUT_SLOT_C);
                progress = 0;
            }
        } else {
            progress = 0;
        }
    }

    private void craftItem(int inputSlot, int outputSlot) {
        ItemStack result = new ItemStack(ModItems.DRYHEMPLEAF.get(), 1);
        this.itemHandler.extractItem(inputSlot, 1, false);
        this.itemHandler.setStackInSlot(outputSlot, new ItemStack(result.getItem(),
                this.itemHandler.getStackInSlot(outputSlot).getCount() + result.getCount()));
    }

    private boolean hasRecipe(int inputSlot, int outputSlot) {
        boolean hasCraftingItem = this.itemHandler.getStackInSlot(inputSlot).getItem() == ModItems.HEMPLEAF.get();
        ItemStack result = new ItemStack(ModItems.DRYHEMPLEAF.get());
        return hasCraftingItem && canInsertAmountIntoOutputSlot(result.getCount(), outputSlot)
                && canInsertItemIntoOutputSlot(result.getItem(), outputSlot);
    }

    private boolean canInsertItemIntoOutputSlot(Item item, int outputSlot) {
        return this.itemHandler.getStackInSlot(outputSlot).isEmpty()
                || this.itemHandler.getStackInSlot(outputSlot).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count, int outputSlot) {
        return this.itemHandler.getStackInSlot(outputSlot).getCount() + count
                <= this.itemHandler.getStackInSlot(outputSlot).getMaxStackSize();
    }


}

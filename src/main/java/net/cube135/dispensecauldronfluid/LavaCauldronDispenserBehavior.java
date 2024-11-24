package net.cube135.dispensecauldronfluid;

import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LavaCauldronDispenserBehavior extends ItemDispenserBehavior {
    private final DispenserBehavior defaultBehavior;

    public LavaCauldronDispenserBehavior() {
        // Save the default dispenser behavior for lava buckets
        this.defaultBehavior = DispenserBlock.BEHAVIORS.get(Items.LAVA_BUCKET);
    }

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        DispenserBlockEntity blockEntity = pointer.blockEntity();
        World world = blockEntity.getWorld();
        BlockPos pos = blockEntity.getPos().offset(pointer.state().get(DispenserBlock.FACING));
        assert world != null;
        if (world.getBlockState(pos).isOf(Blocks.CAULDRON)) {
            // Fill the cauldron with lava
            world.setBlockState(pos, Blocks.LAVA_CAULDRON.getDefaultState());

            if (blockEntity instanceof DispenserBlockEntity dispenser) {
                for (int i = 0; i < dispenser.size(); i++) {
                    ItemStack slotStack = dispenser.getStack(i);
                    if (slotStack.getItem() == Items.LAVA_BUCKET) {
                        // Add empty Bucket and remove Lava Bucket
                        dispenser.addToFirstFreeSlot(new ItemStack(Items.BUCKET));
                        slotStack.decrement(1);

                        break; // Operation complete, no need to iterate further
                    }
                }
            }
            return stack;
        }

        // Fallback to default behavior if no cauldron is in front
        return this.defaultBehavior.dispense(pointer, stack);
    }
}

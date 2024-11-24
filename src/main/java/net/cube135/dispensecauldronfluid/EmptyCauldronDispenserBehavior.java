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

public class EmptyCauldronDispenserBehavior extends ItemDispenserBehavior {
    private final DispenserBehavior defaultBehavior;

    public EmptyCauldronDispenserBehavior() {
        // Save the default dispenser behavior for lava buckets
        this.defaultBehavior = DispenserBlock.BEHAVIORS.get(Items.BUCKET);
    }

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        DispenserBlockEntity blockEntity = pointer.blockEntity();
        World world = blockEntity.getWorld();
        BlockPos pos = blockEntity.getPos().offset(pointer.state().get(DispenserBlock.FACING));
        assert world != null;
        if (world.getBlockState(pos).isOf(Blocks.LAVA_CAULDRON) || world.getBlockState(pos).isOf(Blocks.WATER_CAULDRON)) {
            if (blockEntity instanceof DispenserBlockEntity dispenser) {
                for (int i = 0; i < dispenser.size(); i++) {
                    ItemStack slotStack = dispenser.getStack(i);
                    if (slotStack.getItem() == Items.BUCKET) {
                        // Add Lava Bucket and remove empty Bucket
                        if (world.getBlockState(pos).isOf(Blocks.LAVA_CAULDRON)) {
                            dispenser.addToFirstFreeSlot(new ItemStack(Items.LAVA_BUCKET));
                        } else if (world.getBlockState(pos).isOf(Blocks.WATER_CAULDRON)) {
                            dispenser.addToFirstFreeSlot(new ItemStack(Items.WATER_BUCKET));
                        }
                        slotStack.decrement(1);

                        break; // Operation complete, no need to iterate further
                    }
                }
            }
            // Empty the cauldron
            world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());

            return stack;
        }

        // Fallback to default behavior if no cauldron is in front
        return this.defaultBehavior.dispense(pointer, stack);
    }
}

package net.cube135.dispensecauldronfluid;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WaterCauldronDispenserBehavior extends ItemDispenserBehavior {
    private final DispenserBehavior defaultBehavior;

    public WaterCauldronDispenserBehavior() {
        // Save the default dispenser behavior for lava buckets
        this.defaultBehavior = DispenserBlock.BEHAVIORS.get(Items.WATER_BUCKET);
    }

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        DispenserBlockEntity blockEntity = pointer.blockEntity();
        World world = blockEntity.getWorld();
        BlockPos pos = blockEntity.getPos().offset(pointer.state().get(DispenserBlock.FACING));
        assert world != null;
        if (world.getBlockState(pos).isOf(Blocks.CAULDRON)) {
            // Fill the cauldron with Water
            world.setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState());

            // Increase the WaterLevel to 3 = full cauldron
            BlockState cauldronState = world.getBlockState(pos);
            if (cauldronState.isOf(Blocks.WATER_CAULDRON)) {
                BlockState newState = cauldronState.with(Properties.LEVEL_3, 3);
                world.setBlockState(pos, newState, 3);
            }

            if (blockEntity instanceof DispenserBlockEntity dispenser) {
                for (int i = 0; i < dispenser.size(); i++) {
                    ItemStack slotStack = dispenser.getStack(i);
                    if (slotStack.getItem() == Items.WATER_BUCKET) {
                        // Add empty Bucket and remove Water Bucket
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

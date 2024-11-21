package net.cube135.dispensecauldronfluid;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispenseCauldronFluid implements ModInitializer {
	public static final String MOD_ID = "dispense-cauldron-fluid";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		DispenserBlock.registerBehavior(Items.LAVA_BUCKET, new LavaCauldronDispenserBehavior());
		DispenserBlock.registerBehavior(Items.WATER_BUCKET, new WaterCauldronDispenserBehavior());
		DispenserBlock.registerBehavior(Items.BUCKET, new EmptyCauldronDispenserBehavior());
	}

	public static class LavaCauldronDispenserBehavior extends ItemDispenserBehavior {
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
			}
			return stack;
		}
	}

	public static class WaterCauldronDispenserBehavior extends ItemDispenserBehavior {
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
			}
			return stack;
		}
	}

	public static class EmptyCauldronDispenserBehavior extends ItemDispenserBehavior {
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
			}
			return stack;
		}
	}
}
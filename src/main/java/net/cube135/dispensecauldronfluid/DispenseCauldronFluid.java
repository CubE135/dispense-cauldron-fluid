package net.cube135.dispensecauldronfluid;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Items;
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
}
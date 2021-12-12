package maxhyper.dtbyg.growthlogic;

import com.ferreusveritas.dynamictrees.growthlogic.GrowthLogicKit;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.trees.Species;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PrairieOakLogic extends GrowthLogicKit {
    public PrairieOakLogic(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    public int[] directionManipulation(World world, BlockPos pos, Species species, int radius, GrowSignal signal, int[] probMap) {
        Direction originDir = signal.dir.getOpposite();

        // Can't grow down, set it's chance to 0
        probMap[0] = 0;

        // If we're in the trunk, have a higher chance of branching out. If not, then lower chance
        probMap[2] = probMap[3] = probMap[4] = probMap[5] = signal.isInTrunk() ? 3 : 1;

        // If we're not in the trunk, then disable upwards growth
        if (!signal.isInTrunk()) {
            probMap[1] = 0;
        }

        // Disable the direction we came from
        probMap[originDir.ordinal()] = 0;

        return probMap;
    }

    @Override
    public Direction newDirectionSelected(World world, BlockPos pos, Species species, Direction newDir, GrowSignal signal){
        if (!signal.isInTrunk()) {
            if (signal.energy >= 3.5f) {
                // Reduce energy when not in the trunk, to prevent it from branching too much
                signal.energy *= 0.85;
            }
        }
        return newDir;
    }

    @Override
    public float getEnergy(World world, BlockPos pos, Species species, float signalEnergy) {
        return signalEnergy;
    }

    @Override
    public int getLowestBranchHeight(World world, BlockPos pos, Species species, int lowestBranchHeight) {
        return lowestBranchHeight;
    }
}

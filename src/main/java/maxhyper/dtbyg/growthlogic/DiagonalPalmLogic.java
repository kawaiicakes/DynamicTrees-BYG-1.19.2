package maxhyper.dtbyg.growthlogic;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.growthlogic.PalmGrowthLogic;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DiagonalPalmLogic extends PalmGrowthLogic {

    public DiagonalPalmLogic(ResourceLocation registryName) {
        super(registryName);
    }

    private static final double chanceToDiverge = 0.8;
    private static final double chanceToSplit = 0.06;
    private static final double splitMaxHeightEnergyFactor = 0.5; //can only split under the bottom half

    @Override
    public int[] directionManipulation(World world, BlockPos pos, Species species, int radius, GrowSignal signal, int[] probMap) {
        Direction originDir = signal.dir.getOpposite();

        // Alter probability map for direction change
        probMap[0] = 0; // Down is always disallowed for palm
        probMap[1] = species.getUpProbability();
        // Start by disabling probability on the sides
        probMap[2] = probMap[3] = probMap[4] = probMap[5] =  0;

        int diverge = (int)(4/chanceToDiverge);
        int split = (int)(1/chanceToSplit);
        int randCoordCode = Math.abs(CoordUtils.coordHashCode(pos, 2));

        int directionSelection = randCoordCode % diverge;
        int splitSelection = randCoordCode % split;
        if (directionSelection < 4 && signal.energy > 1){
            Direction selectedDir = Direction.values()[2 + directionSelection];
            //only do branching if it just grew up (to avoid long sideways branches)
            if (originDir == Direction.DOWN){
                probMap[selectedDir.ordinal()] = 10;
                //if the chance to split is met, the clockwise direction is also enabled
                if (splitSelection == 0 && signal.energy > species.getEnergy(world, signal.rootPos) * (1-splitMaxHeightEnergyFactor)){
                    probMap[selectedDir.getClockWise().ordinal()] = 10;
                }
                probMap[1] = 0;
            }
        }

//        boolean found = false;
//        for (Direction dir : Direction.values()){
//            if (dir != signal.dir.getOpposite()){
//                if (TreeHelper.isBranch(world.getBlockState(pos.offset(dir.getNormal())))){
//                    probMap[dir.ordinal()] = 1;
//                    found = true;
//                }
//            }
//        }
//        if (!found){
//            probMap[1] = species.getUpProbability();
//            probMap[2] = probMap[3] = probMap[4] = probMap[5] = 1;
//        }
//
//        int split = (int)(1/chanceToSplit);
//        int randCoordCode = Math.abs(CoordUtils.coordHashCode(pos, 2));
//
//        int splitSelection = randCoordCode % split;
//
//        if (splitSelection == 0 && signal.energy > species.getEnergy(world, signal.rootPos) * (1-splitMaxHeightEnergyFactor)){
//            probMap[selectedDir.getClockWise().ordinal()] = 1;
//        }

        probMap[originDir.ordinal()] = 0; // Disable the direction we came from

        return probMap;
    }

}

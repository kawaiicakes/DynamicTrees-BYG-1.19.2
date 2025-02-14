package maxhyper.dtbyg.init;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.cells.CellKit;
import com.ferreusveritas.dynamictrees.api.registry.TypeRegistryEvent;
import com.ferreusveritas.dynamictrees.api.worldgen.FeatureCanceller;
import com.ferreusveritas.dynamictrees.blocks.branches.BranchBlock;
import com.ferreusveritas.dynamictrees.blocks.leaves.LeavesProperties;
import com.ferreusveritas.dynamictrees.blocks.rootyblocks.SoilHelper;
import com.ferreusveritas.dynamictrees.blocks.rootyblocks.SoilProperties;
import com.ferreusveritas.dynamictrees.blocks.rootyblocks.SpreadableSoilProperties;
import com.ferreusveritas.dynamictrees.growthlogic.GrowthLogicKit;
import com.ferreusveritas.dynamictrees.systems.BranchConnectables;
import com.ferreusveritas.dynamictrees.systems.fruit.Fruit;
import com.ferreusveritas.dynamictrees.systems.genfeatures.GenFeature;
import com.ferreusveritas.dynamictrees.trees.Family;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.CommonVoxelShapes;
import com.google.common.collect.ImmutableList;
import corgiaoc.byg.common.properties.blocks.end.shattereddesert.OddityCactusBlock;
import corgiaoc.byg.common.properties.blocks.nether.warped.WarpedCactusBlock;
import corgiaoc.byg.common.world.feature.config.BYGMushroomConfig;
import corgiaoc.byg.common.world.feature.config.BYGTreeConfig;
import corgiaoc.byg.common.world.feature.config.GiantFlowerConfig;
import corgiaoc.byg.core.BYGBlocks;
import corgiaoc.byg.core.world.BYGBiomes;
import corgiaoc.byg.core.world.BYGConfiguredFeatures;
import corgiaoc.byg.core.world.util.WorldGenRegistrationHelper;
import maxhyper.dtbyg.DynamicTreesBYG;
import maxhyper.dtbyg.blocks.DynamicArisianBloomBranch;
import maxhyper.dtbyg.blocks.LavaSoilProperties;
import maxhyper.dtbyg.blocks.ScruffyLeavesProperties;
import maxhyper.dtbyg.cancellers.BYGFeatureCanceller;
import maxhyper.dtbyg.cancellers.CactusFeatureCanceller;
import maxhyper.dtbyg.cells.DTBYGCellKits;
import maxhyper.dtbyg.fruits.EtherBulbsFruit;
import maxhyper.dtbyg.genfeatures.DTBYGGenFeatures;
import maxhyper.dtbyg.growthlogic.DTBYGGrowthLogicKits;
import maxhyper.dtbyg.trees.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DTBYGRegistries {

    public static Block ARISIAN_BLOOM_BRANCH;

    public static final VoxelShape MUSHROOM_STEM_LONG = Block.box(7D, 0D, 7D, 9D, 10D, 9D);
    public static final VoxelShape SYTHIAN_CAP_A = Block.box(4D, 6D, 4D, 12D, 8D, 12D);
    public static final VoxelShape SYTHIAN_CAP_B = Block.box(5D, 3D, 5D, 11D, 5D, 11D);
    public static final VoxelShape SYTHIAN_CAP_C = Block.box(5D, 9D, 5D, 11D, 11D, 11D);

    public static final VoxelShape SYTHIAN_MUSHROOM = VoxelShapes.or(MUSHROOM_STEM_LONG, SYTHIAN_CAP_A, SYTHIAN_CAP_B, SYTHIAN_CAP_C);

    public static void setup() {
        CommonVoxelShapes.SHAPES.put(DynamicTreesBYG.resLoc("sythian").toString(), SYTHIAN_MUSHROOM);
    }

    public static void setupBlocks() {
        setUpSoils();
        setupConnectables();
    }

    private static void setUpSoils() {
        SoilProperties netherrackProperties = SoilHelper.getProperties(Blocks.NETHERRACK);
        if (netherrackProperties instanceof SpreadableSoilProperties)
            ((SpreadableSoilProperties) netherrackProperties).addSpreadableSoils(
                    BYGBlocks.SYTHIAN_NYLIUM, BYGBlocks.OVERGROWN_NETHERRACK, BYGBlocks.MYCELIUM_NETHERRACK);
    }

    private static void setupConnectables() {
        BranchConnectables.makeBlockConnectable(BYGBlocks.POLLEN_BLOCK, (state, world, pos, side) -> {
            if (side == Direction.DOWN) return 1;
            return 0;
        });

        BranchConnectables.makeBlockConnectable(BYGBlocks.PURPLE_SHROOMLIGHT, (state, world, pos, side) -> {
            if (side == Direction.DOWN) {
                BlockState branchState = world.getBlockState(pos.relative(Direction.UP));
                BranchBlock branch = TreeHelper.getBranch(branchState);
                if (branch != null)
                    return MathHelper.clamp(branch.getRadius(branchState) - 1, 1, 8);
                else return 8;
            }
            return 0;
        });

        BranchConnectables.makeBlockConnectable(ARISIAN_BLOOM_BRANCH, (state, world, pos, side) -> {
            if (state.hasProperty(HorizontalBlock.FACING)) {
                return state.getValue(HorizontalBlock.FACING) == side ? 1 : 0;
            }
            return 0;
        });
    }

    @SubscribeEvent
    public static void onGenFeatureRegistry(final com.ferreusveritas.dynamictrees.api.registry.RegistryEvent<GenFeature> event) {
        DTBYGGenFeatures.register(event.getRegistry());
    }

    @SubscribeEvent
    public static void onCellKitRegistry(final com.ferreusveritas.dynamictrees.api.registry.RegistryEvent<CellKit> event) {
        DTBYGCellKits.register(event.getRegistry());
    }

    @SubscribeEvent
    public static void onGrowthLogicKitRegistry(final com.ferreusveritas.dynamictrees.api.registry.RegistryEvent<GrowthLogicKit> event) {
        DTBYGGrowthLogicKits.register(event.getRegistry());
    }


    @SubscribeEvent
    public static void registerSpeciesTypes(final TypeRegistryEvent<Species> event) {
        event.registerType(DynamicTreesBYG.resLoc("poplar"), PoplarSpecies.TYPE);
        event.registerType(DynamicTreesBYG.resLoc("twiglet"), TwigletSpecies.TYPE);
        event.registerType(DynamicTreesBYG.resLoc("generates_underwater"), GenUnderwaterSpecies.TYPE);
        event.registerType(DynamicTreesBYG.resLoc("generates_on_extra_soil"), GenOnExtraSoilSpecies.TYPE);
        event.registerType(DynamicTreesBYG.resLoc("mangrove"), MangroveSpecies.TYPE);
        event.registerType(DynamicTreesBYG.resLoc("lament"), LamentSpecies.TYPE);
    }

    @SubscribeEvent
    public static void registerFamilyTypes(final TypeRegistryEvent<Family> event) {
        event.registerType(DynamicTreesBYG.resLoc("diagonal_palm"), DiagonalPalmFamily.TYPE);
        event.registerType(DynamicTreesBYG.resLoc("sythian_fungus"), SythianFungusFamily.TYPE);
        event.registerType(DynamicTreesBYG.resLoc("nightshade"), NightshadeFamily.TYPE);
    }

    @SubscribeEvent
    public static void registerLeavesPropertiesTypes(final TypeRegistryEvent<LeavesProperties> event) {
        event.registerType(DynamicTreesBYG.resLoc("scruffy"), ScruffyLeavesProperties.TYPE);
    }

    @SubscribeEvent
    public static void registerSoilPropertiesTypes(final TypeRegistryEvent<SoilProperties> event) {
        event.registerType(DynamicTreesBYG.resLoc("lava"), LavaSoilProperties.TYPE);
    }

    @SubscribeEvent
    public static void registerFruitTypes(final TypeRegistryEvent<Fruit> event) {
        event.registerType(DynamicTreesBYG.resLoc("ether_bulbs"), EtherBulbsFruit.TYPE);
    }

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
        ARISIAN_BLOOM_BRANCH = new DynamicArisianBloomBranch(AbstractBlock.Properties.of(Material.PLANT, MaterialColor.COLOR_PURPLE).instabreak().sound(SoundType.GRASS).noOcclusion().noCollission().harvestTool(ToolType.HOE).lightLevel((state) -> 10));
        ARISIAN_BLOOM_BRANCH.setRegistryName(DynamicTreesBYG.resLoc("arisian_bloom_branch"));
        event.getRegistry().register(ARISIAN_BLOOM_BRANCH);

        setupBlocks();
    }

    public static final FeatureCanceller BYG_TREE_CANCELLER = new BYGFeatureCanceller<>(DynamicTreesBYG.resLoc("tree"), BYGTreeConfig.class);
    public static final FeatureCanceller BYG_FUNGUS_CANCELLER = new BYGFeatureCanceller<>(DynamicTreesBYG.resLoc("fungus"), BYGMushroomConfig.class);
    public static final FeatureCanceller GIANT_FLOWER_CANCELLER = new BYGFeatureCanceller<>(DynamicTreesBYG.resLoc("giant_flower"), GiantFlowerConfig.class);
    public static final FeatureCanceller WARPED_CACTUS_CANCELLER = new CactusFeatureCanceller<>(DynamicTreesBYG.resLoc("warped_cactus"), WarpedCactusBlock.class);
    public static final FeatureCanceller ODDITY_CACTUS_CANCELLER = new CactusFeatureCanceller<>(DynamicTreesBYG.resLoc("oddity_cactus"), OddityCactusBlock.class);

    @SubscribeEvent
    public static void onFeatureCancellerRegistry(final com.ferreusveritas.dynamictrees.api.registry.RegistryEvent<FeatureCanceller> event) {
        event.getRegistry().registerAll(BYG_TREE_CANCELLER);
        event.getRegistry().registerAll(BYG_FUNGUS_CANCELLER);
        event.getRegistry().registerAll(GIANT_FLOWER_CANCELLER);
        event.getRegistry().registerAll(WARPED_CACTUS_CANCELLER);
        event.getRegistry().registerAll(ODDITY_CACTUS_CANCELLER);
    }

    public static final ConfiguredFeature<?, ?> RANDOM_WARPED_DESERT_VEGETATION_NO_CACTI = WorldGenRegistrationHelper.createConfiguredFeature("warped_desert_plants_no_cacti", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of(BYGConfiguredFeatures.WARPED_BUSH.weighted(0.25F), BYGConfiguredFeatures.WARPED_CORAL.weighted(0.25F), BYGConfiguredFeatures.WARPED_CORAL_FAN.weighted(0.25F)), Feature.NO_OP.configured(NoFeatureConfig.INSTANCE))).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(16))));
    public static final ConfiguredFeature<?, ?> RANDOM_ODDITY_PLANT_NO_CACTI = WorldGenRegistrationHelper.createConfiguredFeature("oddity_plants_no_cacti", Feature.RANDOM_SELECTOR.configured(new MultipleRandomFeatureConfig(ImmutableList.of((Feature.NO_OP.configured(NoFeatureConfig.INSTANCE)).weighted(0.5F)), BYGConfiguredFeatures.ODDITY_BUSH)).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(16))));

    public static void onBiomeLoading(final BiomeLoadingEvent event) {
        //The features for the warped desert need to be added back, as cancelling the cacti removes these too.
        if (Objects.equals(BYGBiomes.WARPED_DESERT.getRegistryName(), event.getName())) {
            event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, RANDOM_WARPED_DESERT_VEGETATION_NO_CACTI);
        }
        //Same with the shattered desert in the end
        if (Objects.equals(BYGBiomes.SHATTERED_DESERT.getRegistryName(), event.getName())) {
            event.getGeneration().addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, RANDOM_ODDITY_PLANT_NO_CACTI);
        }
    }

}

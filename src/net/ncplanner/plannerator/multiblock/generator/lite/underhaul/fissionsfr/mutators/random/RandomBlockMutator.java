package net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.mutators.random;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.LiteUnderhaulSFR;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingIndicies;
public class RandomBlockMutator implements Mutator<LiteUnderhaulSFR>{
    public SettingIndicies indicies;
    public RandomBlockMutator(LiteUnderhaulSFR multiblock){
        indicies = new SettingIndicies("Blocks", multiblock.configuration.blockName);
    }
    @Override
    public void run(LiteUnderhaulSFR multiblock, Random rand){
        multiblock.blocks[rand.nextInt(multiblock.dims[0])][rand.nextInt(multiblock.dims[1])][rand.nextInt(multiblock.dims[2])] = indicies.get()[rand.nextInt(indicies.get().length)];
    }
}
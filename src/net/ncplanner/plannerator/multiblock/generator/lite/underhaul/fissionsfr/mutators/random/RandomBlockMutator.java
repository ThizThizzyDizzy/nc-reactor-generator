package net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.mutators.random;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.LiteUnderhaulSFR;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingIndicies;
public class RandomBlockMutator implements Mutator<LiteUnderhaulSFR>{
    public SettingIndicies indicies;
    public RandomBlockMutator(LiteUnderhaulSFR multiblock){
        indicies = new SettingIndicies("Blocks", multiblock.configuration.blockDisplayName, multiblock.configuration.blockDisplayTexture, "Air");
    }
    @Override
    public String getTitle(){
        return "Random Block Mutator";
    }
    @Override
    public String getTooltip(){
        return "Changes a random block in the reactor to a random block from the list of allowed blocks";
    }
    @Override
    public void run(LiteUnderhaulSFR multiblock, Random rand){
        multiblock.blocks[rand.nextInt(multiblock.dims[0])][rand.nextInt(multiblock.dims[1])][rand.nextInt(multiblock.dims[2])] = indicies.get()[rand.nextInt(indicies.get().length)]-1;
    }
    @Override
    public int getSettingCount(){
        return 1;
    }
    @Override
    public Setting getSetting(int i){
        return indicies;
    }
}
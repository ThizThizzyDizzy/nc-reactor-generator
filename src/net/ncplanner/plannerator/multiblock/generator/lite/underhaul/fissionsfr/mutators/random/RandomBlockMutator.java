package net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.mutators.random;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.LiteUnderhaulSFR;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingIndicies;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingSymmetry;
public class RandomBlockMutator implements Mutator<LiteUnderhaulSFR>{
    public SettingIndicies indicies;
    public SettingSymmetry symmetry = new SettingSymmetry();
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
        int block = indicies.get()[rand.nextInt(indicies.get().length)]-1;
        symmetry.get().apply(rand.nextInt(multiblock.dims[0]), rand.nextInt(multiblock.dims[1]), rand.nextInt(multiblock.dims[2]), multiblock.dims[0], multiblock.dims[1], multiblock.dims[2], (x, y, z) -> {
            multiblock.blocks[x][y][z] = block;
        });
    }
    @Override
    public int getSettingCount(){
        return 2;
    }
    @Override
    public Setting getSetting(int i){
        if(i==1)return symmetry;
        return indicies;
    }
}
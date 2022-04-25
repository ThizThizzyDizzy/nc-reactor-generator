package net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.mutators;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.LiteUnderhaulSFR;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
public class ClearInvalidMutator implements Mutator<LiteUnderhaulSFR>{
    @Override
    public String getTitle(){
        return "Clear Invalid Mutator";
    }
    @Override
    public String getTooltip(){
        return "Changes a random block in the reactor to a random block from the list of allowed blocks";
    }
    @Override
    public void run(LiteUnderhaulSFR multiblock, Random rand){
        if(multiblock.blockValid==null)return;//no validity to clear
        for(int x = 0; x<multiblock.dims[0]; x++){
            for(int y = 0; y<multiblock.dims[1]; y++){
                for(int z = 0; z<multiblock.dims[2]; z++){
                    if(multiblock.blockValid[x][y][z]+multiblock.blockEfficiency[x][y][z]<=0)multiblock.blocks[x][y][z] = -1;
                }
            }
        }
    }
    @Override
    public int getSettingCount(){
        return 0;
    }
    @Override
    public Setting getSetting(int i){
        return null;
    }
}
package net.ncplanner.plannerator.multiblock.generator.lite.mutator;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
public class StandardMutator<T extends LiteMultiblock> extends GeneratorMutator<T>{
    public StandardMutator(Mutator<T> mutator){
        super(mutator);
    }
    @Override
    public String getTitle(){
        return "Standard Mutator";
    }
    @Override
    public String getTooltip(){
        return "Runs once per iteration";
    }
    @Override
    public void run(T multiblock, Random rand){
        mutator.run(multiblock, rand);
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
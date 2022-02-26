package net.ncplanner.plannerator.multiblock.generator.lite.mutator;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingInt;
public class RandomQuantityMutator<T extends LiteMultiblock> extends GeneratorMutator<T>{
    public SettingInt min = new SettingInt("Minimum tries", 1);
    public SettingInt max = new SettingInt("Maximum tries", 100);
    public RandomQuantityMutator(Mutator<T> mutator){
        super(mutator);
    }
    @Override
    public String getTitle(){
        return "Random Quantity Mutator";
    }
    @Override
    public String getTooltip(){
        return "Runs a random number of times per iteration\nEvenly distributed between min and max";
    }
    @Override
    public void run(T multiblock, Random rand){
        int tries = rand.nextInt(max.get()-min.get())+min.get();
        for(int i = 0; i<tries; i++){
            mutator.run(multiblock, rand);
        }
    }
    @Override
    public int getSettingCount(){
        return 2;
    }
    @Override
    public Setting getSetting(int i){
        switch(i){
            case 0:
                return min;
            case 1:
                return max;
        }
        return null;
    }
}
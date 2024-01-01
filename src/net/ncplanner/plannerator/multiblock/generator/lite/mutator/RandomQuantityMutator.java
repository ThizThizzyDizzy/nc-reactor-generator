package net.ncplanner.plannerator.multiblock.generator.lite.mutator;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingVariable;
public class RandomQuantityMutator<T extends LiteMultiblock> extends GeneratorMutator<T>{
    public SettingVariable<Integer> min = new SettingVariable<>("Minimum tries", new ConstInt(1));
    public SettingVariable<Integer> max = new SettingVariable<>("Maximum tries", new ConstInt(100));
    public RandomQuantityMutator(){
        super("random_quantity");
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
        int tries = rand.nextInt(max.get().get()-min.get().get()+1)+min.get().get();
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
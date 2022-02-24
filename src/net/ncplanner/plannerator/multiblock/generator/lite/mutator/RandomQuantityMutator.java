package net.ncplanner.plannerator.multiblock.generator.lite.mutator;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingInt;
public class RandomQuantityMutator<T extends LiteMultiblock> extends GeneratorMutator<T>{
    public SettingInt min = new SettingInt("Minimum tries", 1);
    public SettingInt max = new SettingInt("Maximum tries", 100);
    public RandomQuantityMutator(Mutator<T> mutator){
        super(mutator);
    }
    @Override
    public void run(T multiblock, Random rand){
        int tries = rand.nextInt(max.get()-min.get())+min.get();
        for(int i = 0; i<tries; i++){
            mutator.run(multiblock, rand);
        }
    }
}
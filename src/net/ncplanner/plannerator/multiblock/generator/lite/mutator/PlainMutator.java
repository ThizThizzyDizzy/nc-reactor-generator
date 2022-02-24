package net.ncplanner.plannerator.multiblock.generator.lite.mutator;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
public class PlainMutator<T extends LiteMultiblock> extends GeneratorMutator<T>{
    public PlainMutator(Mutator<T> mutator){
        super(mutator);
    }
    @Override
    public void run(T multiblock, Random rand){
        mutator.run(multiblock, rand);
    }
}
package net.ncplanner.plannerator.multiblock.generator.lite.mutator;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
public interface Mutator<T extends LiteMultiblock>{
    public void run(T multiblock, Random rand);
}
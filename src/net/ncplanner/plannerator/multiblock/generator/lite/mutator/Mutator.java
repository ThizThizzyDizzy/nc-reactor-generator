package net.ncplanner.plannerator.multiblock.generator.lite.mutator;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.ThingWithSettings;
public interface Mutator<T extends LiteMultiblock> extends ThingWithSettings{
    public String getTitle();
    public String getTooltip();
    public void run(T multiblock, Random rand);
}
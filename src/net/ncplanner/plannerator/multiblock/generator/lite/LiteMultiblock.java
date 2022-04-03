package net.ncplanner.plannerator.multiblock.generator.lite;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
public interface LiteMultiblock<T extends Multiblock> extends ThingWithVariables{
    public void importAndConvert(T multiblock);
    public String getTooltip();
    public void calculate();
    public void getMutators(ArrayList<Supplier<Mutator>> mutators);
    public LiteMultiblock<T> copy();
}
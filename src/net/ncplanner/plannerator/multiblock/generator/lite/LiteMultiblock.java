package net.ncplanner.plannerator.multiblock.generator.lite;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
public interface LiteMultiblock<T extends Multiblock>{
    public void importAndConvert(T multiblock);
    public String getTooltip();
    public int getVariableCount();
    public Variable getVariable(int i);
    public void getMutators(ArrayList<Supplier<Mutator>> mutators);
}
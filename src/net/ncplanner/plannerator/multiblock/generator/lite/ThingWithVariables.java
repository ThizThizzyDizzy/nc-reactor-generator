package net.ncplanner.plannerator.multiblock.generator.lite;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
public interface ThingWithVariables{
    public int getVariableCount();
    public Variable getVariable(int i);
}
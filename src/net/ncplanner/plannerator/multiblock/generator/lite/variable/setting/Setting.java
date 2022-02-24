package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
public interface Setting<T> extends Variable<T>{
    public void set(T value);
}
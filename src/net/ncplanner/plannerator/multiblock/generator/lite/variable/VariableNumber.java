package net.ncplanner.plannerator.multiblock.generator.lite.variable;
public abstract class VariableNumber<T extends Number> implements Variable<T>{
    @Override
    public abstract String getName();
    @Override
    public abstract T get();
}
package net.ncplanner.plannerator.multiblock.generator.lite.variable;
public abstract class VariableLong extends VariableNumber<Long>{
    private final String name;
    public VariableLong(String name){
        this.name = name;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public Long get(){
        return getValue();
    }
    public abstract long getValue();
}
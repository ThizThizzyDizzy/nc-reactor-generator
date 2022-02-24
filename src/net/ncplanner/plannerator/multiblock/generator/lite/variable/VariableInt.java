package net.ncplanner.plannerator.multiblock.generator.lite.variable;
public abstract class VariableInt extends VariableNumber<Integer>{
    private final String name;
    public VariableInt(String name){
        this.name = name;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public Integer get(){
        return getValue();
    }
    public abstract int getValue();
}
package net.ncplanner.plannerator.multiblock.generator.lite.variable;
public abstract class VariableBoolean implements Variable<Boolean>{
    private final String name;
    public VariableBoolean(String name){
        this.name = name;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public Boolean get(){
        return getValue();
    }
    public abstract boolean getValue();
}
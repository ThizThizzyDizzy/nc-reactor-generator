package net.ncplanner.plannerator.multiblock.generator.lite.variable;
public abstract class VariableFloat implements VariableNumber<Float>{
    private final String name;
    public VariableFloat(String name){
        this.name = name;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public Float get(){
        return getValue();
    }
    public abstract float getValue();
}
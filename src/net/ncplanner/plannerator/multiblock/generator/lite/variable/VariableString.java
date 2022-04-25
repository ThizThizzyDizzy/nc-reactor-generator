package net.ncplanner.plannerator.multiblock.generator.lite.variable;
public abstract class VariableString implements Variable<String>{
    private final String name;
    public VariableString(String name){
        this.name = name;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public String get(){
        return getValue();
    }
    public abstract String getValue();
}
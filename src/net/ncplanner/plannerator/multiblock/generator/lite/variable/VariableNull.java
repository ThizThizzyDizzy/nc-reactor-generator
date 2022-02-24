package net.ncplanner.plannerator.multiblock.generator.lite.variable;
public class VariableNull implements Variable<Object>{
    @Override
    public String getName(){
        return "null";
    }
    @Override
    public Object get(){
        return null;
    }
}
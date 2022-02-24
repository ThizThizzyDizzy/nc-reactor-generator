package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
public class SettingIndicies implements Variable<int[]>, Setting<int[]>{
    private final String name;
    private int[] value;
    public final String[] names;
    public SettingIndicies(String name, int[] value, String[] names){
        this.name = name;
        this.value = value;
        this.names = names;
    }
    public SettingIndicies(String name, String[] names){
        this(name, gen(names), names);
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public void set(int[] value){
        this.value = value;
    }
    @Override
    public int[] get(){
        return value;
    }
    private static int[] gen(String[] names){
        int[] indicies = new int[names.length];
        for(int i = 0; i<indicies.length; i++)indicies[i] = i;
        return indicies;
    }
}
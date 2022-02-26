package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
public class SettingConditionList implements Setting<ArrayList<Condition>>{
    private final String name;
    private ArrayList<Condition> value;
    public SettingConditionList(String name, ArrayList<Condition> value){
        this.name = name;
        this.value = value;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public void set(ArrayList<Condition> value){
        this.value = value;
    }
    @Override
    public ArrayList<Condition> get(){
        return value;
    }
    @Override
    public void addSettings(SingleColumnList list, MenuGenerator menu){
        menu.addConditionSettings(value);//assume it's not null, nothing will ever go wrong here
    }
}
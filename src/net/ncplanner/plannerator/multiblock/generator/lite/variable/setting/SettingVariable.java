package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuSelect;
public class SettingVariable<T> implements Setting<Variable<T>>{
    private final String name;
    private Variable<T> value;
    public SettingVariable(String name, Variable<T> value){
        this.name = name;
        this.value = value;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public void set(Variable<T> value){
        this.value = value;
    }
    @Override
    public Variable<T> get(){
        return value;
    }
    private String ttip;
    @Override
    public void addSettings(SingleColumnList stageSettings, MenuGenerator menu){
        ArrayList<Variable> rawVars = new ArrayList<>();
        ArrayList<String> rawNames = new ArrayList<>();
        menu.getAllVariables(rawVars, rawNames);
        ArrayList<Variable<T>> vars = new ArrayList<>();
        ArrayList<String> varNames = new ArrayList<>();
        for(int i = 0; i<rawVars.size(); i++){
            try{
                vars.add(rawVars.get(i));
                varNames.add(rawNames.get(i));
            }catch(ClassCastException ex){}
        }
        if(value==null){
            stageSettings.add(new Button(0, 0, 0, 24, "Choose variable", true).addAction(() -> {
                new MenuSelect<Variable<T>>(menu.gui, menu, vars, varNames, (var)->{
                    value = var;
                    menu.rebuildGUI();
                }).open();
            }));
        }else{
            stageSettings.add(new Label(0, 0, 0, 24, value.getName()){
                Button modify = add(new Button(0, 0, height, height, "?", true, true).addAction(() -> {
                    new MenuSelect<Variable<T>>(menu.gui, menu, vars, varNames, (var)->{
                        value = var;
                        ttip = varNames.get(vars.indexOf(var));
                        menu.rebuildGUI();
                    }).open();
                }));
                @Override
                public void draw(double deltaTime){
                    modify.x = width-modify.width;
                    super.draw(deltaTime);
                }
            }.setTooltip(ttip));
        }
    }
}
package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstFloat;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.ConstInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.Constant;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.Operator;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorAddition;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorDivision;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorFloor;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorMaximum;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorMinimum;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorMultiplication;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.OperatorSubtraction;
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
        vars.add(null);
        ArrayList<String> varNames = new ArrayList<>();
        varNames.add("Constants and Operators");
        for(int i = 0; i<rawVars.size(); i++){
            try{
                vars.add(rawVars.get(i));
                varNames.add(rawNames.get(i));
            }catch(ClassCastException ex){}
        }
        if(value==null){
            stageSettings.add(new Button(0, 0, 0, 24, "Choose variable", true).addAction(() -> {
                new MenuSelect<>(menu.gui, menu, vars, varNames, (var)->{
                    if(var==null){
                        selectConstant(menu);
                        return;
                    }
                    value = var;
                    menu.rebuildGUI();
                }).open();
            }));
        }else{
            stageSettings.add(new Label(0, 0, 0, 24, value.getName()){
                Button modify = add(new Button(0, 0, height, height, "?", true, true).addAction(() -> {
                    new MenuSelect<>(menu.gui, menu, vars, varNames, (var)->{
                        if(var==null){
                            selectConstant(menu);
                            return;
                        }
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
            if(value instanceof Constant)((Constant)value).addSettings(stageSettings, menu);
            if(value instanceof Operator)menu.addSettings((Operator)value);
        }
    }
    private void selectConstant(MenuGenerator menu){
        ArrayList<Variable> allConsts = new ArrayList<>();
        ArrayList<String> allNames = new ArrayList<>();
        allConsts.add(new ConstInt(0));
        allNames.add("Integer");
        allConsts.add(new ConstFloat(0));
        allNames.add("Float");
        allConsts.add(new OperatorAddition());
        allNames.add("Addition");
        allConsts.add(new OperatorSubtraction());
        allNames.add("Subtraction");
        allConsts.add(new OperatorMultiplication());
        allNames.add("Multiplication");
        allConsts.add(new OperatorDivision());
        allNames.add("Division");
        allConsts.add(new OperatorMinimum());
        allNames.add("Minimum");
        allConsts.add(new OperatorMaximum());
        allNames.add("Maximum");
        allConsts.add(new OperatorFloor());
        allNames.add("Floor");
        ArrayList<Variable<T>> constants = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        for(int i = 0; i<allConsts.size(); i++){
            try{
                constants.add(allConsts.get(i));
                names.add(allNames.get(i));
            }catch(ClassCastException ex){}
        }
        new MenuSelect<>(menu.gui, menu, constants, names, (constant)->{
            value = constant;
            ttip = null;
            menu.rebuildGUI();
        }).open();
    }
}
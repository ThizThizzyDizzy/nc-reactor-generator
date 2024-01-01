package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.constant.Constant;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.operator.Operator;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
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
    public void set(Variable value){
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
            Label label = stageSettings.add(new Label(0, 0, 0, 24, value.getName()){
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
            if(value instanceof Operator)menu.addSettings(label, (Operator)value);
        }
    }
    private void selectConstant(MenuGenerator menu){
        ArrayList<Variable> allConsts = new ArrayList<>();
        ArrayList<String> allNames = new ArrayList<>();
        Constant.registeredConstants.forEach((key, val) -> {
            allNames.add(key);
            allConsts.add((Variable)val.get());
        });
        Operator.registeredOperators.forEach((key, val) -> {
            allNames.add(key);
            allConsts.add((Variable)val.get());
        });
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
    public Variable convertFromObject(NCPFObject ncpf){
        String type = ncpf.getString("type");
        String subtype = ncpf.getString(type);
        switch(type){
            case "constant":
                Supplier<Constant> cons = Constant.registeredConstants.get(subtype);
                if(cons==null)throw new IllegalArgumentException("Failed to load unregistered constant: "+subtype);
                Constant constant = cons.get();
                constant.convertFromObject(ncpf);
                return (Variable)constant;
            case "operator":
                Supplier<Operator> oper = Operator.registeredOperators.get(subtype);
                if(oper==null)throw new IllegalArgumentException("Failed to load unregistered operator: "+subtype);
                Operator operator = oper.get();
                operator.convertFromObject(ncpf);
                return (Variable)operator;
            case "variable":
                Variable var = MenuGenerator.current.getVariable(subtype);
                if(var==null)throw new RuntimeException("Failed to load invalid variable: "+subtype);
                return var;

            default:
                throw new IllegalArgumentException("Unknown variable type: "+type+"!");
        }
    }
    public NCPFObject convertToObject(){
        NCPFObject ncpf = new NCPFObject();
        String type = "variable";
        if(value instanceof Operator){
            Operator operator = (Operator)value;
            type = "operator";
            ncpf.setString(type, operator.getType());
            operator.convertToObject(ncpf);
        }else if(value instanceof Constant){
            Constant constant = (Constant)value;
            type = "constant";
            ncpf.setString(type, constant.getType());
            constant.convertToObject(ncpf);
        }else{
            String k = MenuGenerator.current.getVariableName(value);
            if(k==null)throw new RuntimeException("Variable key is missing!");
            ncpf.setString(type, k);
        }
        ncpf.setString("type", type);
        return ncpf;
    }
}
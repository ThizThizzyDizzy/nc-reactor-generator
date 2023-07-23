package net.ncplanner.plannerator.multiblock.generator.lite.variable.constant;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableInt;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
public class ConstInt extends VariableInt implements Constant{
    public ConstInt(){
        super("Constant");
    }
    public ConstInt(int value){
        this();
        this.value = value;
    }
    public int value;
    @Override
    public int getValue(){
        return value;
    }
    @Override
    public void addSettings(SingleColumnList list, MenuGenerator menu){
        list.add(new TextBox(0, 0, 0, 32, value+"", true, "Integer", 10){
            {
                onChange(() -> {
                    value = Integer.parseInt(text);
                });
            }
        }.setIntFilter());
    }
    @Override
    public String getType(){
        return "int";
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("value", value);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        value = ncpf.getInteger("value");
    }
}
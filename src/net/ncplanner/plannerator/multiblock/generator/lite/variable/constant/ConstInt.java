package net.ncplanner.plannerator.multiblock.generator.lite.variable.constant;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableInt;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
public class ConstInt extends VariableInt implements Constant{
    public ConstInt(int value){
        super("Constant");
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
}
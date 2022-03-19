package net.ncplanner.plannerator.multiblock.generator.lite.variable.constant;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableFloat;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
public class ConstFloat extends VariableFloat implements Constant{
    public ConstFloat(float value){
        super("Constant");
        this.value = value;
    }
    public float value;
    @Override
    public float getValue(){
        return value;
    }
    @Override
    public void addSettings(SingleColumnList list, MenuGenerator menu){
        list.add(new TextBox(0, 0, 0, 32, value+"", true, "Float", 10){
            {
                onChange(() -> {
                    value = Float.parseFloat(text);
                });
            }
        }.setFloatFilter());
    }
}
package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableNumber;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
public class SettingFloat extends Parameter<Float> implements VariableNumber<Float>, Setting<Float>{
    private float value = 0;
    public SettingFloat(){
        super("float");
    }
    public SettingFloat(String name, float value){
        this();
        this.name = name;
        this.value = value;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public Float get(){
        return value;
    }
    @Override
    public void set(Float value){
        this.value = value;
    }
    @Override
    public void addSettings(SingleColumnList list, MenuGenerator menu){
        list.add(new TextBox(0, 0, 0, 32, value+"", true, name, 10){
            {
                onChange(() -> {
                    set(Float.parseFloat(text));
                });
            }
        }.setFloatFilter());
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        name = ncpf.getString("name");
        value = ncpf.getFloat("value");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("name", name);
        ncpf.setFloat("value", value);
    }
}
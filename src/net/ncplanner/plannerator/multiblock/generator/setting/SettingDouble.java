package net.ncplanner.plannerator.multiblock.generator.setting;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
public class SettingDouble implements Setting<Double>{
    protected final String name;
    protected final Double min;
    protected final Double max;
    protected final String tooltip;
    protected double value;
    public SettingDouble(String name, Double min, Double max, double defaultValue, String tooltip){
        this.name = name;
        this.min = min;
        this.max = max;
        this.tooltip = tooltip;
        this.value = defaultValue;
    }
    @Override
    public Double getValue(){
        return value;
    }
    @Override
    public void setValue(Double value){
        this.value = value;
    }
    @Override
    public void buildComponents(SingleColumnList generatorSettings){
        generatorSettings.add(new TextBox(0, 0, 0, 48, value+"", true, name){
            @Override
            public void onKeyEvent(int key, int scancode, int action, int mods){
                super.onKeyEvent(key, scancode, action, mods);
                value = Double.parseDouble(text);
            }
            @Override
            public void onCharTyped(char c){
                super.onCharTyped(c);
                value = Double.parseDouble(text);
            }
        }.setDoubleFilter(min, max).setTooltip(tooltip));
    }
}
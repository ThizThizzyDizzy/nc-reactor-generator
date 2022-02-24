package net.ncplanner.plannerator.multiblock.generator.setting;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
public class SettingPercentage extends SettingDouble{
    public SettingPercentage(String name, double defaultValue, String tooltip){
        super(name, 0d, 100d, defaultValue*100, tooltip);
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
        }.setDoubleFilter(min, max).setTooltip(tooltip).setSuffix("%"));
    }
    @Override
    public Double getValue(){
        return super.getValue()/100;
    }
    @Override
    public void setValue(Double value){
        super.setValue(value*100);
    }
}
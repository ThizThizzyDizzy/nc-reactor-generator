package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuPickCondition;
public class SettingCondition implements Setting<Condition>{
    private final String name;
    private Condition value;
    public SettingCondition(String name, Condition value){
        this.name = name;
        this.value = value;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public void set(Condition value){
        this.value = value;
    }
    @Override
    public Condition get(){
        return value;
    }
    @Override
    public void addSettings(SingleColumnList stageSettings, MenuGenerator menu){
        if(value==null){
            stageSettings.add(new Button(0, 0, 0, 24, "Choose condition", true).addAction(() -> {
                new MenuPickCondition(menu.gui, menu, (condition)->{
                    value = condition;
                    menu.rebuildGUI();
                }).open();
            }));
        }else{
            stageSettings.add(new Label(0, 0, 0, 24, value.getTitle()){
                Button modify = add(new Button(0, 0, height, height, "?", true, true).addAction(() -> {
                    new MenuPickCondition(menu.gui, menu, (condition)->{
                        value = condition;
                        menu.rebuildGUI();
                    }).open();
                }));
                @Override
                public void draw(double deltaTime){
                    modify.x = width-modify.width;
                    super.draw(deltaTime);
                }
            }.setTooltip(value.getTooltip()));
            menu.addSettings(value);
        }
    }
}
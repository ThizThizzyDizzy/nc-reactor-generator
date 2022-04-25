package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingBoolean;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingFloat;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingInt;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ExpandingGridLayout;
public class MenuPickVariable<T extends LiteMultiblock> extends MenuDialog{
    private final ArrayList<Button> buttons = new ArrayList<>();
    private final Consumer<Setting> onConfirm;
    public MenuPickVariable(GUI gui, Menu parent, T multiblock, Consumer<Setting> onConfirm){
        super(gui, parent);
        this.onConfirm = onConfirm;
        minWidth = minHeight = 0;
        addVar("Boolean", (name)->{return new SettingBoolean(name, false);});
        addVar("Int", (name)->{return new SettingInt(name, 0);});
        addVar("Float", (name)->{return new SettingFloat(name, 0);});
        setTitle("Choose a setting");
        setContent(new ExpandingGridLayout(192, 64, 3).addAll(buttons));
        addButton("Cancel", () -> {
            close();
        });
    }
    public void addVar(String text, Function<String, Setting> func){
        buttons.add(new Button(0, 0, 0, 0, text, true).addAction(() -> {
            new MenuInputDialog(gui, parent, "", "Choose Variable Name").addButton("Cancel", true).addButton("Done", (name) -> {
                onConfirm.accept(func.apply(name));
            }, true).open();
        }));
    }
}
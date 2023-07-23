package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Parameter;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ExpandingGridLayout;
public class MenuPickParameter<T extends LiteMultiblock> extends MenuDialog{
    private final ArrayList<Button> buttons = new ArrayList<>();
    private final Consumer<Parameter> onConfirm;
    public MenuPickParameter(GUI gui, Menu parent, T multiblock, Consumer<Parameter> onConfirm){
        super(gui, parent);
        this.onConfirm = onConfirm;
        minWidth = minHeight = 0;
        Parameter.registeredParameters.forEach((key, val) -> {
            addVar(key, val);
        });
        setTitle("Choose a parameter");
        setContent(new ExpandingGridLayout(192, 64, 3).addAll(buttons));
        addButton("Cancel", () -> {
            close();
        });
    }
    public void addVar(String text, Supplier<Parameter> func){
        buttons.add(new Button(text, true).addAction(() -> {
            new MenuInputDialog(gui, parent, "", "Choose Parameter Name").addButton("Cancel", true).addButton("Done", (name) -> {
                Parameter setting = func.get();
                setting.name = name;
                onConfirm.accept(func.get());
            }, true).open();
        }));
    }
}
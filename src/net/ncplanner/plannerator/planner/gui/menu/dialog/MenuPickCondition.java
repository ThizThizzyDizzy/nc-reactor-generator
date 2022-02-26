package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ExpandingGridLayout;
public class MenuPickCondition extends MenuDialog{
    private final ArrayList<Button> buttons = new ArrayList<>();
    public MenuPickCondition(GUI gui, Menu parent, Consumer<Condition> onConfirm){
        super(gui, parent);
        minWidth = minHeight = 0;
        for(Supplier<Condition> supplier : Condition.conditions){
            Condition condition = supplier.get();
            buttons.add(new Button(0, 0, 0, 0, condition.getTitle(), true).setTooltip(condition.getTooltip()).addAction(() -> {
                close();
                onConfirm.accept(condition);
            }));
        }
        setContent(new ExpandingGridLayout(128, 64, 2).addAll(buttons));
        addButton("Cancel", () -> {
            close();
        });
    }
}
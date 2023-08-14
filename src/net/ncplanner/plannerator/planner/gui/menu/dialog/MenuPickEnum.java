package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.legacy.LegacyExpandingGridLayout;
public class MenuPickEnum<T extends Enum> extends MenuDialog{
    private final ArrayList<Button> buttons = new ArrayList<>();
    public MenuPickEnum(GUI gui, Menu parent, T[] options, Consumer<T> onConfirm){
        super(gui, parent);
        minWidth = minHeight = 0;
        for(T t : options){
            buttons.add(new Button(t.toString(), true).addAction(() -> {
                onConfirm.accept(t);
                close();
                gui.menu.onOpened();
            }));
        }
        setTitle("Choose one");
        setContent(new LegacyExpandingGridLayout(192, 64, 3).addAll(buttons));
        addButton("Cancel", () -> {
            close();
        });
    }
}
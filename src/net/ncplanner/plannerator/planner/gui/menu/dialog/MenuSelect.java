package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.legacy.LegacySingleColumnGridLayout;
public class MenuSelect<T> extends MenuDialog{
    private final ArrayList<Button> buttons = new ArrayList<>();
    public MenuSelect(GUI gui, Menu parent, ArrayList<T> options, ArrayList<String> names, Consumer<T> onConfirm){
        super(gui, parent);
        minWidth = minHeight = 0;
        for(int i = 0; i<options.size(); i++){
            T t = options.get(i);
            buttons.add(new Button(names.get(i), true).addAction(() -> {
                close();
                onConfirm.accept(t);
            }));
        }
        setContent(new LegacySingleColumnGridLayout(32).addAll(buttons));
        content.width = 640;
        addButton("Cancel", () -> {
            close();
        });
    }
}
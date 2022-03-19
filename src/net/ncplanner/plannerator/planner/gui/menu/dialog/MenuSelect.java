package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.SingleColumnGridLayout;
public class MenuSelect<T> extends MenuDialog{
    private final ArrayList<Button> buttons = new ArrayList<>();
    private final ArrayList<T> options;
    private final ArrayList<String> names;
    public MenuSelect(GUI gui, Menu parent, ArrayList<T> options, ArrayList<String> names, Consumer<T> onConfirm){
        super(gui, parent);
        minWidth = minHeight = 0;
        for(int i = 0; i<options.size(); i++){
            T t = options.get(i);
            buttons.add(new Button(0, 0, 0, 0, names.get(i), true).addAction(() -> {
                close();
                onConfirm.accept(t);
            }));
        }
        setContent(new SingleColumnGridLayout(32).addAll(buttons));
        content.width = 512;
        addButton("Cancel", () -> {
            close();
        });
        this.options = options;
        this.names = names;
    }
}
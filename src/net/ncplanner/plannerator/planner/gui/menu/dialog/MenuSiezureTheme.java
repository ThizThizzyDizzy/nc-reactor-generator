package net.ncplanner.plannerator.planner.gui.menu.dialog;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
public class MenuSiezureTheme extends MenuDialog{
    public MenuSiezureTheme(GUI gui, Menu parent, Runnable onYes, Runnable onNo){
        super(gui, parent);
        textBox.setText("CONTAINS LOTS OF FLASHING COLORS\nCONTINUE?");
        addButton("Yes", () -> {
            close();
            onYes.run();
        });
        addButton("No", () -> {
            close();
            onNo.run();
        });
    }
}
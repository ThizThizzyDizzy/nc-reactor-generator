package planner.menu.dialog;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuSiezureTheme extends MenuDialog{
    public MenuSiezureTheme(GUI gui, Menu parent, Runnable onYes, Runnable onNo){
        super(gui, parent);
        textBox.setText("CONTAINS LOTS OF FLASHING COLORS\nCONTINUE?");
        addButton("Yes", (e) -> {
            close();
            onYes.run();
        });
        addButton("No", (e) -> {
            close();
            onNo.run();
        });
    }
}
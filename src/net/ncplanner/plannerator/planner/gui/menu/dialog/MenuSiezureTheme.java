package net.ncplanner.plannerator.planner.gui.menu.dialog;
public class MenuSiezureTheme extends MenuDialog{
    public MenuSiezureTheme(Runnable onYes, Runnable onNo){
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
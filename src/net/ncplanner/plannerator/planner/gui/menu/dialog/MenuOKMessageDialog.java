package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.Random;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
public class MenuOKMessageDialog extends MenuMessageDialog{
    private static final String[] extraPossibilities = new String[]{"Got it", "Thanks", "Great", "Cool", "Alright", "Yep", "Awknowledged", "Aye", "Ignore", "Skip"};
    private static final Random rand = new Random();
    public MenuOKMessageDialog(GUI gui, Menu parent, String message){
        super(gui, parent, message);
        addButton(rand.nextDouble()<.01?extraPossibilities[rand.nextInt(extraPossibilities.length)]:"OK", true);
    }
}
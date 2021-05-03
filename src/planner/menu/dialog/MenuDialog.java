package planner.menu.dialog;
import java.util.ArrayList;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistScrollable;
import planner.menu.component.MenuComponentTextDisplay;
import simplelibrary.opengl.gui.ActionListener;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuDialog extends Menu{
    private MenuComponentMinimalistScrollable textPanel = add(new MenuComponentMinimalistScrollable(0, 0, 0, 0, 16, 16));
    public MenuComponentTextDisplay textBox = textPanel.add(new MenuComponentTextDisplay(""));
    public double maxWidth = 0.5d;
    public double maxHeight = 0.5d;
    public double minWidth = 0.25d;
    public double minHeight = 0.1d;
    public int border = 8;
    public int buttonHeight = 64;
    private ArrayList<MenuComponentMinimalistButton> buttons = new ArrayList<>();
    public MenuDialog(GUI gui, Menu parent){
        super(gui, parent);
    }
    @Override
    public void render(int millisSinceLastTick){
        try{
            if(parent!=null)parent.render(millisSinceLastTick);
        }catch(Exception ignored){}
        Core.applyAverageColor(Core.theme.getTextColor(), Core.theme.getBackgroundColor());
        double w = Math.max(gui.helper.displayWidth()*minWidth, Math.min(gui.helper.displayWidth()*maxWidth, textBox.width));
        double h = Math.max(gui.helper.displayHeight()*minHeight, Math.min(gui.helper.displayHeight()*maxHeight, textBox.height));
        drawRect(gui.helper.displayWidth()/2-w/2-border, gui.helper.displayHeight()/2-h/2-border, gui.helper.displayWidth()/2+w/2+border, gui.helper.displayHeight()/2+h/2+border+buttonHeight, 0);
        Core.applyColor(Core.theme.getEditorListBorderColor());
        drawRect(gui.helper.displayWidth()/2-w/2, gui.helper.displayHeight()/2-h/2, gui.helper.displayWidth()/2+w/2, gui.helper.displayHeight()/2+h/2, 0);
        Core.applyWhite();
        textPanel.x = gui.helper.displayWidth()/2-w/2;
        textPanel.y = gui.helper.displayHeight()/2-h/2;
        textPanel.width = w;
        textPanel.height = h;
        for(int i = 0; i<buttons.size(); i++){
            buttons.get(i).width = w/buttons.size();
            buttons.get(i).x = gui.helper.displayWidth()/2-w/2+buttons.get(i).width*i;
            buttons.get(i).y = gui.helper.displayHeight()/2+h/2;
        }
        super.render(millisSinceLastTick);
    }
    public void close(){
        gui.menu = parent;
    }
    public void addButton(String text, ActionListener onClick){
        MenuComponentMinimalistButton b = new MenuComponentMinimalistButton(0, 0, 0, buttonHeight, text, true, true);
        b.addActionListener(onClick);
        buttons.add(add(b));
    }
}
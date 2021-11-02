package net.ncplanner.plannerator.planner.menu;
import net.ncplanner.plannerator.discord.Bot;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuDiscord extends Menu{
    MenuComponentMinimalistButton exit = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Exit", true, true));
    public MenuDiscord(GUI gui){
        super(gui, null);
        exit.addActionListener((e) -> {
            gui.helper.running = false;
        });
    }
    @Override
    public void renderBackground(){
        exit.width = gui.helper.displayWidth();
        exit.height = gui.helper.displayHeight();
        Bot.render2D();
    }
}
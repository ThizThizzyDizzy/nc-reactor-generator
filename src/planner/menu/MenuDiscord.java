package planner.menu;
import discord.Bot;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import planner.menu.Menu;
public class MenuDiscord extends Menu{
    MenuComponentMinimalistButton exit = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Exit", true, true));
    public MenuDiscord(GUI gui){
        super(gui, null);
        exit.addActionListener((e) -> {
            Core.helper.running = false;
        });
    }
    @Override
    public void renderBackground(){
        exit.width = Core.helper.displayWidth();
        exit.height = Core.helper.displayHeight();
        Bot.render2D();
    }
}
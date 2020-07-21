package planner.menu;
import discord.Bot;
import org.lwjgl.opengl.Display;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
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
        exit.width = Display.getWidth();
        exit.height = Display.getHeight();
        Bot.render2D();
    }
}
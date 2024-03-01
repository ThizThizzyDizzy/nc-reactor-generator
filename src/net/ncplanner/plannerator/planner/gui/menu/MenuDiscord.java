package net.ncplanner.plannerator.planner.gui.menu;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import static org.lwjgl.glfw.GLFW.*;
public class MenuDiscord extends Menu{
    Button exit = add(new Button("Exit", true, true));
    public MenuDiscord(GUI gui){
        super(gui, null);
        exit.addAction(() -> {
            glfwSetWindowShouldClose(Core.window, true);
        });
    }
    @Override
    public void drawBackground(double deltaTime){
        super.drawBackground(deltaTime);
        exit.width = gui.getWidth();
        exit.height = gui.getHeight();
    }
}
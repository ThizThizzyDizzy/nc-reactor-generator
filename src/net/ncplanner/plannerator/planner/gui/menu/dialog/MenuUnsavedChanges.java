package net.ncplanner.plannerator.planner.gui.menu.dialog;
import com.thizthizzydizzy.dizzyengine.DizzyEngine;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import org.lwjgl.glfw.GLFW;
public class MenuUnsavedChanges extends MenuDialog{
    public MenuUnsavedChanges(GUI gui, Menu parent){
        super(gui, parent);
        textBox.setText("Would you like to save?");
        addButton("Save", () -> {
            new MenuSaveDialog(gui, this, () -> {
                Core.saved = true;
                GLFW.glfwSetWindowShouldClose(DizzyEngine.window, true);
            }).open();
        }, true);
        addButton("Don't Save", () -> {
            Core.saved = true;
            GLFW.glfwSetWindowShouldClose(DizzyEngine.window, true);
        }, true);
        addButton("Cancel", () -> {
            close();
        });
    }
}

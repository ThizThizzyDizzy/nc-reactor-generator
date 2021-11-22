package net.ncplanner.plannerator.planner.gui.menu;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
import net.ncplanner.plannerator.planner.gui.menu.component.MulticolumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.generator.MenuComponentGeneratorListBlock;
import static org.lwjgl.glfw.GLFW.*;
public class MenuGenerator extends Menu{
    private Multiblock<Block> multiblock;
    public static final int partSize = 48;
    public static final int partsWide = 7;
    private final Button done = add(new Button(0, 0, 0, 0, "Done", true, true)).setTooltip("Finish generation and return to the editor");
    public final MulticolumnList parts = add(new MulticolumnList(0, 0, 0, 0, partSize, partSize, partSize/2));
    public final TextBox partsSearch = add(new TextBox(0, 0, 0, 0, "-port", true, "Search"){
        @Override
        public void onCharTyped(char c){
            super.onCharTyped(c);
            refreshPartsList();
        }
        @Override
        public void onKeyEvent(int key, int scancode, int action, int mods){
            super.onKeyEvent(key, scancode, action, mods);
            refreshPartsList();
        }
        @Override
        public void onMouseButton(double x, double y, int button, int action, int mods){
            super.onMouseButton(x, y, button, action, mods);
            if(button==GLFW_MOUSE_BUTTON_RIGHT&&action==GLFW_PRESS){
                text = "";
                refreshPartsList();
                MenuGenerator.this.focusedComponent = this;
                isFocused = true;
            }
        }
    });
    public MenuGenerator(GUI gui, MenuEdit editor, Multiblock<Block> multiblock){
        super(gui, editor);
        this.multiblock = multiblock;
    }
    public synchronized void refreshPartsList(){
        List<Block> availableBlocks = multiblock.getAvailableBlocks();
        ArrayList<Block> searchedAvailable = Pinnable.searchAndSort(availableBlocks, partsSearch.text);
        parts.components.clear();
        for(Block availableBlock : searchedAvailable){
            parts.add(new MenuComponentGeneratorListBlock(this, availableBlock));
        }
    }
}
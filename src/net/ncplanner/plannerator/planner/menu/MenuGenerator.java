package net.ncplanner.plannerator.planner.menu;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimalistButton;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimalistTextBox;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMulticolumnMinimaList;
import net.ncplanner.plannerator.planner.menu.component.generator.MenuComponentGeneratorListBlock;
import org.lwjgl.glfw.GLFW;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuGenerator extends Menu{
    private Multiblock<Block> multiblock;
    public static final int partSize = 48;
    public static final int partsWide = 7;
    private final MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true)).setTooltip("Finish generation and return to the editor");
    public final MenuComponentMulticolumnMinimaList parts = add(new MenuComponentMulticolumnMinimaList(0, 0, 0, 0, partSize, partSize, partSize/2));
    public final MenuComponentMinimalistTextBox partsSearch = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, "-port", true, "Search"){
        @Override
        public void onCharTyped(char c){
            super.onCharTyped(c);
            refreshPartsList();
        }
        @Override
        public void keyEvent(int key, int scancode, boolean isPress, boolean isRepeat, int modifiers){
            super.keyEvent(key, scancode, isPress, isRepeat, modifiers);
            refreshPartsList();
        }
        @Override
        public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
            super.onMouseButton(x, y, button, pressed, mods);
            if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT&&pressed){
                text = "";
                refreshPartsList();
                MenuGenerator.this.selected = this;
                isSelected = true;
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
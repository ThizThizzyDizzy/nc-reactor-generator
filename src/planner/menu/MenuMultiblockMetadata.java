package planner.menu;
import planner.menu.component.MenuComponentMulticolumnMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import java.util.ArrayList;
import planner.Core;
import multiblock.Block;
import multiblock.Multiblock;
import simplelibrary.opengl.gui.GUI;
import planner.menu.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuMultiblockMetadata extends Menu{
    MenuComponentMulticolumnMinimaList list = add(new MenuComponentMulticolumnMinimaList(0, 0, 0, 0, 0, 50, 50));
    MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true).setTooltip("Finish editing metadata"));
    private final Multiblock multiblock;
    public MenuMultiblockMetadata(GUI gui, Menu parent, Multiblock multiblock){
        super(gui, parent);
        this.multiblock = multiblock;
        done.addActionListener((e) -> {
            multiblock.resetMetadata();
            for(int i = 0; i<list.components.size(); i+=2){
                MenuComponentMinimalistTextBox key = (MenuComponentMinimalistTextBox) list.components.get(i);
                MenuComponentMinimalistTextBox value = (MenuComponentMinimalistTextBox) list.components.get(i+1);
                if(key.text.trim().isEmpty()&&value.text.trim().isEmpty())continue;
                multiblock.metadata.put(key.text, value.text);
            }
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SlideTransition.slideFrom(0, 1), 4));
        });
    }
    @Override
    public void renderBackground(){
        list.width = Core.helper.displayWidth();
        list.y = Core.helper.displayHeight()/16;
        list.height = Core.helper.displayHeight()-Core.helper.displayHeight()/8;
        list.columnWidth = (list.width-(list.hasVertScrollbar()?list.vertScrollbarWidth:0))/2;
        done.width = Core.helper.displayWidth();
        done.y = Core.helper.displayHeight()-Core.helper.displayHeight()/16;
        done.height = Core.helper.displayHeight()/16;
    }
    @Override
    public void render(int millisSinceLastTick){
        Core.applyColor(Core.theme.getMetadataPanelBackgroundColor());
        drawRect(0, 0, Core.helper.displayWidth(), Core.helper.displayHeight(), 0);
        Core.applyColor(Core.theme.getMetadataPanelHeaderColor());
        drawRect(0, 0, Core.helper.displayWidth(), Core.helper.displayHeight()/16, 0);
        Core.applyColor(Core.theme.getTextColor());
        drawCenteredText(0, 0, Core.helper.displayWidth(), Core.helper.displayHeight()/16, "Metadata");
        super.render(millisSinceLastTick);
    }
    @Override
    public void onGUIOpened(){
        list.components.clear();
        for(String key : ((Multiblock<Block>)multiblock).metadata.keySet()){
            String value = ((Multiblock<Block>)multiblock).metadata.get(key);
            list.add(new MenuComponentMinimalistTextBox(0,0,0,0,key, true));
            list.add(new MenuComponentMinimalistTextBox(0,0,0,0,value, true));
        }
    }
    @Override
    public void tick(){
        ArrayList<simplelibrary.opengl.gui.components.MenuComponent> remove = new ArrayList<>();
        boolean add = list.components.isEmpty();
        for(int i = 0; i<list.components.size(); i+=2){
            MenuComponentMinimalistTextBox key = (MenuComponentMinimalistTextBox) list.components.get(i);
            MenuComponentMinimalistTextBox value = (MenuComponentMinimalistTextBox) list.components.get(i+1);
            if(i==list.components.size()-2){//the last one
                if(!(key.text.trim().isEmpty()&&value.text.trim().isEmpty())){
                    add = true;
                }
            }else{
                if(key.text.trim().isEmpty()&&value.text.trim().isEmpty()){
                    remove.add(key);
                    remove.add(value);
                }
            }
        }
        list.components.removeAll(remove);
        if(add){
            list.add(new MenuComponentMinimalistTextBox(0,0,0,0,"", true));
            list.add(new MenuComponentMinimalistTextBox(0,0,0,0,"", true));
        }
    }
}
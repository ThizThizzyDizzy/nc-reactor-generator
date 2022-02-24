package net.ncplanner.plannerator.planner.gui.menu;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.MulticolumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
public class MenuMultiblockMetadata extends Menu{
    MulticolumnList list = add(new MulticolumnList(0, 0, 0, 0, 0, 50, 50));
    Button done = add(new Button(0, 0, 0, 0, "Done", true, true).setTooltip("Finish editing metadata"));
    private final Multiblock multiblock;
    public MenuMultiblockMetadata(GUI gui, Menu parent, Multiblock multiblock){
        super(gui, parent);
        this.multiblock = multiblock;
        done.addAction(() -> {
            multiblock.resetMetadata();
            for(int i = 0; i<list.components.size(); i+=2){
                TextBox key = (TextBox) list.components.get(i);
                TextBox value = (TextBox) list.components.get(i+1);
                if(key.text.trim().isEmpty()&&value.text.trim().isEmpty())continue;
                multiblock.metadata.put(key.text, value.text);
            }
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SlideTransition.slideFrom(0, 1), 4));
        });
    }
    @Override
    public void drawBackground(double deltaTime){
        list.width = gui.getWidth();
        list.y = gui.getHeight()/16;
        list.height = gui.getHeight()-gui.getHeight()/8;
        list.columnWidth = (list.width-(list.hasVertScrollbar()?list.vertScrollbarWidth:0))/2;
        done.width = gui.getWidth();
        done.y = gui.getHeight()-gui.getHeight()/16;
        done.height = gui.getHeight()/16;
    }
    @Override
    public void render2d(double deltaTime){
        ArrayList<Component> remove = new ArrayList<>();
        boolean add = list.components.isEmpty();
        for(int i = 0; i<list.components.size(); i+=2){
            TextBox key = (TextBox) list.components.get(i);
            TextBox value = (TextBox) list.components.get(i+1);
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
            list.add(new TextBox(0,0,0,0,"", true));
            list.add(new TextBox(0,0,0,0,"", true));
        }
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getMetadataPanelBackgroundColor());
        renderer.fillRect(0, 0, gui.getWidth(), gui.getHeight());
        renderer.setColor(Core.theme.getMetadataPanelHeaderColor());
        renderer.fillRect(0, 0, gui.getWidth(), gui.getHeight()/16);
        renderer.setColor(Core.theme.getMetadataPanelTextColor());
        renderer.drawCenteredText(0, 0, gui.getWidth(), gui.getHeight()/16, "Metadata");
        super.render2d(deltaTime);
    }
    @Override
    public void onOpened(){
        list.components.clear();
        for(String key : ((Multiblock<Block>)multiblock).metadata.keySet()){
            String value = ((Multiblock<Block>)multiblock).metadata.get(key);
            list.add(new TextBox(0,0,0,0,key, true));
            list.add(new TextBox(0,0,0,0,value, true));
        }
    }
}
package generator;
import java.util.ArrayList;
import multiblock.Multiblock;
import planner.Core;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.component.MenuComponentPriority;
import simplelibrary.opengl.gui.components.MenuComponent;
public class StandardGenerator extends MultiblockGenerator{
    @Override
    public ArrayList<ArrayList<Multiblock>> getMultiblockLists(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public Multiblock[] getValidMultiblocks(){
        return Core.multiblockTypes.toArray(new Multiblock[Core.multiblockTypes.size()]);
    }
    @Override
    public String getName(){
        return "Standard";
    }
    @Override
    public void addSettings(MenuComponentMinimaList generatorSettings, Multiblock multi){
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Final Reactors", true));
        MenuComponentMinimalistTextBox finalReactors = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, "2", true).setIntFilter());
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Working Reactors", true));
        MenuComponentMinimalistTextBox workingReactors = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, "6", true).setIntFilter());
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Timeout (sec)", true));
        MenuComponentMinimalistTextBox timeout = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, "10", true).setIntFilter());
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Priorities", true));
        ArrayList<Priority> priorities = multi.getGenerationPriorities();
        MenuComponentMinimaList prioritiesList = generatorSettings.add(new MenuComponentMinimaList(0, 0, 0, priorities.size()*32, 24){
            @Override
            public void render(int millisSinceLastTick){
                for(MenuComponent c : components){
                    c.width = width-(hasVertScrollbar()?vertScrollbarWidth:0);
                }
                super.render(millisSinceLastTick);
            }
        });
        for(Priority priority : priorities){
            prioritiesList.add(new MenuComponentPriority(priority));
        }
        //move up/move down
        //symmetry, conductorize
        //float change chance
        //bool variable rate
        //bool lock core
        //bool fill air
//        throw new UnsupportedOperationException("Not supported yet.");
    }
}
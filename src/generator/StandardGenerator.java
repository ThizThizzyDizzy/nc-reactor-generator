package generator;
import java.util.ArrayList;
import multiblock.Multiblock;
import planner.Core;
import planner.menu.component.MenuComponentMinimaList;
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
    public void addSettings(MenuComponentMinimaList generatorSettings){
        //float change chance
        //bool variable rate
        //bool lock core
        //bool fill air
//        throw new UnsupportedOperationException("Not supported yet.");
    }
}
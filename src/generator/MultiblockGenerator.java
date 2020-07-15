package generator;
import java.util.ArrayList;
import multiblock.Multiblock;
import planner.menu.component.MenuComponentMinimaList;
public abstract class MultiblockGenerator{
    public static final ArrayList<MultiblockGenerator> generators = new ArrayList<>();
    static{
        generators.add(new StandardGenerator());
    }
    public abstract ArrayList<ArrayList<Multiblock>> getMultiblockLists();
    public abstract Multiblock[] getValidMultiblocks();
    public abstract String getName();
    public static ArrayList<MultiblockGenerator> getGenerators(Multiblock m){
        ArrayList<MultiblockGenerator> valid = new ArrayList<>();
        for(MultiblockGenerator gen : generators){
            for(Multiblock mb : gen.getValidMultiblocks()){
                if(mb.getDefinitionName().equals(m.getDefinitionName()))valid.add(gen);
            }
        }
        return valid;
    }
    public abstract void addSettings(MenuComponentMinimaList generatorSettings);
}
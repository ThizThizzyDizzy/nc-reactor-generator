package planner.multiblock.overhaul.fissionsfr;
import java.util.List;
import planner.Core;
import planner.configuration.Configuration;
import planner.configuration.overhaul.fissionsfr.CoolantRecipe;
import planner.multiblock.Multiblock;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigNumberList;
public class OverhaulSFR extends Multiblock<Block>{
    public CoolantRecipe coolantRecipe;
    public OverhaulSFR(){
        this(7, 5, 7, Core.configuration.overhaul.fissionSFR.coolantRecipes.get(0));
    }
    public OverhaulSFR(int x, int y, int z, CoolantRecipe coolantRecipe){
        super(x, y, z);
        this.coolantRecipe = coolantRecipe;
    }
    @Override
    public String getDefinitionName(){
        return "Overhaul SFR";
    }
    @Override
    public OverhaulSFR newInstance(){
        return new OverhaulSFR();
    }
    @Override
    public void getAvailableBlocks(List<Block> blocks){
        if(Core.configuration==null||Core.configuration.overhaul==null||Core.configuration.overhaul.fissionSFR==null)return;
        for(planner.configuration.overhaul.fissionsfr.Block block : Core.configuration.overhaul.fissionSFR.blocks){
            blocks.add(new Block(-1, -1, -1, block));
        }
    }
    @Override
    public int getMinSize(){
        return Core.configuration.overhaul.fissionSFR.minSize;
    }
    @Override
    public int getMaxSize(){
        return Core.configuration.overhaul.fissionSFR.maxSize;
    }
    @Override
    public void calculate(){
    }
    @Override
    protected Block newCasing(int x, int y, int z){
        return new Block(x, y, z, null);
    }
    @Override
    public String getTooltip(){
        return null;
    }
    @Override
    public int getMultiblockID(){
        return 1;
    }
    @Override
    protected void save(Configuration configuration, Config config){
        ConfigNumberList size = new ConfigNumberList();
        size.add(getX());
        size.add(getY());
        size.add(getZ());
        config.set("size", size);
        boolean compact = isCompact(configuration);//find perfect compression ratio
        config.set("compact", compact);
        ConfigNumberList blox = new ConfigNumberList();
        if(compact){
            for(int x = 0; x<getX(); x++){
                for(int y = 0; y<getY(); y++){
                    for(int z = 0; z<getZ(); z++){
                        Block block = getBlock(x, y, z);
                        if(block==null)blox.add(0);
                        else blox.add(configuration.overhaul.fissionSFR.blocks.indexOf(block.template)+1);
                    }
                }
            }
        }else{
            for(Block block : getBlocks()){
                blox.add(block.x);
                blox.add(block.y);
                blox.add(block.z);
                blox.add(configuration.overhaul.fissionSFR.blocks.indexOf(block.template)+1);
            }
        }
        ConfigNumberList fuels = new ConfigNumberList();
        ConfigNumberList sources = new ConfigNumberList();
        ConfigNumberList irradiatorRecipes = new ConfigNumberList();
        for(Block block : getBlocks()){
            if(block.template.fuelCell)fuels.add(configuration.overhaul.fissionSFR.fuels.indexOf(block.fuel));
            if(block.template.fuelCell)sources.add(configuration.overhaul.fissionSFR.sources.indexOf(block.source)+1);
            if(block.template.irradiator)irradiatorRecipes.add(configuration.overhaul.fissionSFR.irradiatorRecipes.indexOf(block.recipe)+1);
        }
        config.set("blocks", blox);
        config.set("fuels", fuels);
        config.set("sources", sources);
        config.set("irradiatorRecipes", irradiatorRecipes);
    }
    private boolean isCompact(Configuration configuration){
        int blockCount = getBlocks().size();
        int volume = getX()*getY()*getZ();
        int bitsPerDim = logBase(2, Math.max(getX(), Math.max(getY(), getZ())));
        int bitsPerType = logBase(2, configuration.overhaul.fissionSFR.blocks.size());
        int compactBits = bitsPerType*volume;
        int spaciousBits = 4*Math.max(bitsPerDim, bitsPerType)*blockCount;
        return compactBits<spaciousBits;
    }
    private static int logBase(int base, int n){
        return (int)(Math.log(n)/Math.log(base));
    }
    @Override
    public void convertTo(Configuration to){
        for(Block block : getBlocks()){
            if(block.template.fuelCell)block.fuel = to.overhaul.fissionSFR.convert(block.fuel);
            if(block.template.fuelCell)block.source = to.overhaul.fissionSFR.convert(block.source);
            if(block.template.irradiator)block.recipe = to.overhaul.fissionSFR.convert(block.recipe);
            block.template = to.overhaul.fissionSFR.convert(block.template);
        }
    }
}
package net.ncplanner.plannerator.multiblock.generator.lite.underhaulSFR.mutators.random;
import java.util.ArrayList;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.Symmetry;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaulSFR.CompiledUnderhaulSFRConfiguration;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaulSFR.LiteUnderhaulSFR;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingIndicies;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingSymmetry;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.UnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement;
public class RandomBlockMutator extends Mutator<LiteUnderhaulSFR>{
    public SettingIndicies indicies = new SettingIndicies("Blocks");
    public SettingSymmetry symmetry = new SettingSymmetry();
    public RandomBlockMutator(){
        super("nuclearcraft:underhaul_sfr:random_block");
    }
    public RandomBlockMutator(LiteUnderhaulSFR multiblock){
        this();
        setIndicies(multiblock);
    }
    @Override
    public String getTitle(){
        return "Random Block Mutator";
    }
    @Override
    public String getTooltip(){
        return "Changes a random block in the reactor to a random block from the list of allowed blocks";
    }
    @Override
    public void run(LiteUnderhaulSFR multiblock, Random rand){
        int block = indicies.get()[rand.nextInt(indicies.get().length)]-1;
        symmetry.get().apply(rand.nextInt(multiblock.dims[0]), rand.nextInt(multiblock.dims[1]), rand.nextInt(multiblock.dims[2]), multiblock.dims[0], multiblock.dims[1], multiblock.dims[2], (x, y, z) -> {
            multiblock.blocks[x][y][z] = block;
        });
    }
    @Override
    public int getSettingCount(){
        return 2;
    }
    @Override
    public Setting getSetting(int i){
        if(i==1)return symmetry;
        return indicies;
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        indicies.set(ncpf.getIntArray("indicies"));
        symmetry.set(ncpf.getDefinedNCPFObject("symmetry", Symmetry::new));
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setIntArray("indicies", indicies.get());
        ncpf.setDefinedNCPFObject("symmetry", symmetry.get());
    }
    @Override
    public void setIndicies(LiteUnderhaulSFR multiblock){
        indicies.init(multiblock.configuration.blockDisplayName, multiblock.configuration.blockDisplayTexture, "Air");
    }
    @Override
    public void init(LiteUnderhaulSFR multiblock){}
    @Override
    public void importFrom(LiteUnderhaulSFR multiblock, NCPFConfigurationContainer container){
        UnderhaulSFRConfiguration config = container.getConfiguration(UnderhaulSFRConfiguration::new);
        ArrayList<BlockElement> templates = new ArrayList<>();
        boolean hasAir = false;
        for(int i : indicies.get()){
            if(i==0){
                hasAir = true;
                continue;
            }
            templates.add(config.blocks.get(i-1));
        }
        setIndicies(multiblock);
        ArrayList<Integer> idxs = new ArrayList<>();
        if(hasAir)idxs.add(0);
        for(int i = 0; i<multiblock.configuration.blockDefinition.length; i++){
            for(BlockElement block : templates){
                if((block.cooler!=null)==(multiblock.configuration.blockCooling[i]!=0)
                        &&(block.fuelCell!=null)==(multiblock.configuration.blockFuelCell[i])
                        &&(block.moderator!=null)==(multiblock.configuration.blockModerator[i])
                        &&(block.activeCooler!=null)==(multiblock.configuration.blockActive[i]!=null)){
                    idxs.add(i+1);
                }
            }
        }
        int[] indxs = new int[idxs.size()];
        for(int i = 0; i<idxs.size(); i++)indxs[i] = idxs.get(i);
        this.indicies.set(indxs);
    }
}
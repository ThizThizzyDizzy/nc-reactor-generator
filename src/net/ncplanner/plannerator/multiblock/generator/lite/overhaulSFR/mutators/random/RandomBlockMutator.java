package net.ncplanner.plannerator.multiblock.generator.lite.overhaulSFR.mutators.random;
import java.util.ArrayList;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.Symmetry;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.multiblock.generator.lite.overhaulSFR.LiteOverhaulSFR;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingIndicies;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingSymmetry;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement;
public class RandomBlockMutator extends Mutator<LiteOverhaulSFR>{
    public SettingIndicies indicies = new SettingIndicies("Blocks");
    public SettingSymmetry symmetry = new SettingSymmetry();
    public RandomBlockMutator(){
        super("nuclearcraft:overhaul_sfr:random_block");
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
    public void run(LiteOverhaulSFR multiblock, Random rand){
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
    public void setIndicies(LiteOverhaulSFR multiblock){
        indicies.init(multiblock.configuration.blockDisplayName, multiblock.configuration.blockDisplayTexture, "Air");
    }
    @Override
    public void init(LiteOverhaulSFR multiblock){}
    @Override
    public void importFrom(LiteOverhaulSFR multiblock, NCPFConfigurationContainer container){
        OverhaulSFRConfiguration config = container.getConfiguration(OverhaulSFRConfiguration::new);
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
                if((block.heatsink!=null)==(multiblock.configuration.blockCooling[i]!=0)
                        &&(block.fuelCell!=null)==(multiblock.configuration.blockFuelCell[i])
                        &&(block.moderator!=null)==(multiblock.configuration.blockModerator[i])
                        &&(block.irradiator!=null)==(multiblock.configuration.blockIrradiator[i])
                        &&(block.reflector!=null)==(multiblock.configuration.blockReflector[i])
                        &&(block.neutronShield!=null)==(multiblock.configuration.blockShield[i])
                        &&(block.conductor!=null)==(multiblock.configuration.blockConductor[i])){
                    idxs.add(i+1);
                }
            }
        }
        int[] indxs = new int[idxs.size()];
        for(int i = 0; i<idxs.size(); i++)indxs[i] = idxs.get(i);
        this.indicies.set(indxs);
    }
}
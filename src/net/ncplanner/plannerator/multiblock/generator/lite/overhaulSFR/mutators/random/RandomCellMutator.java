package net.ncplanner.plannerator.multiblock.generator.lite.overhaulSFR.mutators.random;
import java.util.ArrayList;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.generator.lite.Symmetry;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.Mutator;
import net.ncplanner.plannerator.multiblock.generator.lite.overhaulSFR.LiteOverhaulSFR;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingBoolean;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingIndicies;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingSymmetry;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement;
public class RandomCellMutator extends Mutator<LiteOverhaulSFR>{
    public SettingIndicies indicies = new SettingIndicies("Blocks");
    public SettingBoolean addModerators = new SettingBoolean("Fill moderators to adjacent cells", true);
    public SettingBoolean useReflectors = new SettingBoolean("Add reflectors", true);
    public SettingBoolean additionalCells = new SettingBoolean("Add additional cells to produce a grid", true);
    public SettingSymmetry symmetry = new SettingSymmetry();
    public RandomCellMutator(){
        super("nuclearcraft:overhaul_sfr:random_cell");
    }
    public RandomCellMutator(LiteOverhaulSFR multiblock){
        this();
        setIndicies(multiblock);
    }
    @Override
    public String getTitle(){
        return "Random Cell Mutator";
    }
    @Override
    public String getTooltip(){
        return "Adds a fuel cell at a random point in the reactor, optionally with moderator lines or other cells to ensure it can be valid";
    }
    @Override
    public void run(LiteOverhaulSFR multiblock, Random rand){
        ArrayList<Integer> cellBlocks = new ArrayList<>();
        ArrayList<Integer> moderatorBlocks = new ArrayList<>();
        ArrayList<Integer> reflectorBlocks = new ArrayList<>();
        for(int i : indicies.get()){
            if(multiblock.configuration.blockFuelCell[i])cellBlocks.add(i);
            if(multiblock.configuration.blockModerator[i])
                moderatorBlocks.add(i);
            if(multiblock.configuration.blockReflector[i])
                reflectorBlocks.add(i);
        }
        ArrayList<BlockPos> positions = new ArrayList<>();
        ArrayList<Integer> blocks = new ArrayList<>();
        int targetX = rand.nextInt(multiblock.dims[0]);
        int targetY = rand.nextInt(multiblock.dims[1]);
        int targetZ = rand.nextInt(multiblock.dims[2]);
        //these offsets are the number of moderators to be placed between this cell and the other one in this direction
        int xOffset = 0;
        int yOffset = 0;
        int zOffset = 0;
        if(additionalCells.get()){
            xOffset = rand.nextInt(multiblock.configuration.neutronReach*2+1)-multiblock.configuration.neutronReach;
            yOffset = rand.nextInt(multiblock.configuration.neutronReach*2+1)-multiblock.configuration.neutronReach;
            zOffset = rand.nextInt(multiblock.configuration.neutronReach*2+1)-multiblock.configuration.neutronReach;
            //constrain such that it will never try to put stuff outside the reactor
            xOffset = Math.min(multiblock.dims[0]-targetX-2, Math.max(xOffset, 1-targetX));
            yOffset = Math.min(multiblock.dims[1]-targetY-2, Math.max(yOffset, 1-targetY));
            zOffset = Math.min(multiblock.dims[2]-targetZ-2, Math.max(zOffset, 1-targetZ));
        }
        int x1 = targetX;
        int y1 = targetY;
        int z1 = targetZ;
        int x2 = targetX;
        int y2 = targetY;
        int z2 = targetZ;
        if(xOffset<0)x1 += xOffset-1;
        if(xOffset>0)x2 += xOffset+1;
        if(yOffset<0)y1 += yOffset-1;
        if(yOffset>0)y2 += yOffset+1;
        if(zOffset<0)z1 += zOffset-1;
        if(zOffset>0)z2 += zOffset+1;
        BlockPos.forEachInCell(x1, y1, z1, x2, y2, z2, (x, y, z) -> {
            positions.add(new BlockPos(x, y, z));
            blocks.add(cellBlocks.get(rand.nextInt(cellBlocks.size())));
        }, (x, y, z) -> {
            positions.add(new BlockPos(x, y, z));
            blocks.add(moderatorBlocks.get(rand.nextInt(moderatorBlocks.size())));
        }, null, null);
        for(int X = 0; X<=(xOffset==0?0:1); X++){//each corner
            int x = targetX+(X>0?Math.max(0, xOffset):Math.min(0, xOffset));
            for(int Y = 0; Y<=(yOffset==0?0:1); Y++){
                int y = targetY+(Y>0?Math.max(0, yOffset):Math.min(0, yOffset));
                for(int Z = 0; Z<=(zOffset==0?0:1); Z++){
                    int z = targetZ+(Z>0?Math.max(0, zOffset):Math.min(0, zOffset));
                    positions.add(new BlockPos(targetX, targetY, targetZ));
                    blocks.add(cellBlocks.get(rand.nextInt(cellBlocks.size())));
                }
            }
        }
        //target cell
        positions.add(new BlockPos(targetX, targetY, targetZ));
        blocks.add(cellBlocks.get(rand.nextInt(cellBlocks.size())));

        for(int i = 0; i<positions.size(); i++){
            BlockPos pos = positions.get(i);
            int block = blocks.get(i);
            symmetry.get().apply(pos.x, pos.y, pos.z, multiblock.dims[0], multiblock.dims[1], multiblock.dims[2], (x, y, z) -> {
                multiblock.blocks[x][y][z] = block;
            });
        }
    }
    @Override
    public int getSettingCount(){
        return 5;
    }
    @Override
    public Setting getSetting(int i){
        switch(i){
            case 0:
                return indicies;
            case 1:
                return addModerators;
            case 2:
                return useReflectors;
            case 3:
                return additionalCells;
            case 4:
                return symmetry;
            default:
                throw new IllegalArgumentException("Invalid Setting ID: "+i);
        }
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        indicies.set(ncpf.getIntArray("indicies"));
        addModerators.set(ncpf.getBoolean("add_moderators"));
        useReflectors.set(ncpf.getBoolean("use_reflectors"));
        additionalCells.set(ncpf.getBoolean("additional_cells"));
        symmetry.set(ncpf.getDefinedNCPFObject("symmetry", Symmetry::new));
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setIntArray("indicies", indicies.get());
        ncpf.setBoolean("add_moderators", addModerators.get());
        ncpf.setBoolean("use_reflectors", useReflectors.get());
        ncpf.setBoolean("additional_cells", additionalCells.get());
        ncpf.setDefinedNCPFObject("symmetry", symmetry.get());
    }
    @Override
    public void setIndicies(LiteOverhaulSFR multiblock){
        indicies.init(multiblock.configuration.blockDisplayName, multiblock.configuration.blockDisplayTexture, "Air");
    }
    @Override
    public void init(LiteOverhaulSFR multiblock){
    }
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

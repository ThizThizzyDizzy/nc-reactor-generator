package net.ncplanner.plannerator.multiblock.generator.lite.overhaulSFR;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.IrradiatorRecipe;
public class LiteOverhaulSFR implements LiteMultiblock<OverhaulSFR>{
    public final CompiledOverhaulSFRConfiguration configuration;
    public int[][][] sourceValid;
    public int[][][] propogated;
    public int[][][] neutronFlux;
    public int[][][] hadFlux;
    public int[][][] moderatorLines;
    public float[][][] positionalEfficiency;
    public int[][][] blockActive;
    public int[][][] moderatorValid;
    //TODO block stats stuff
    private final int[] dims;
    public final int[][][] blocks;
    
    public int coolantRecipe;
    
    //TODO stats stuff
    private int[] blockCount;
    private int[][] heatsinkCalculationStepIndicies;
    public LiteOverhaulSFR(CompiledOverhaulSFRConfiguration configuration){
        this.configuration = configuration;
        blocks = new int[configuration.maxSize][configuration.maxSize][configuration.maxSize];
        blockCount = new int[configuration.blockDefinition.length];//only initialized this early for variables
        for(int x = 0; x<configuration.maxSize; x++){
            for(int y = 0; y<configuration.maxSize; y++){
                for(int z = 0; z<configuration.maxSize; z++){
                    blocks[x][y][z] = -1;
                }
            }
        }
        dims = new int[]{configuration.minSize,configuration.minSize,configuration.minSize};//default to minimum size
    }
    public void countBlocks(){
        blockCount = new int[configuration.blockDefinition.length];
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    if(blocks[x][y][z]>=0)blockCount[blocks[x][y][z]]++;
                }
            }
        }
    }
    public void optimizeHeatsinkSteps(){
        int steps = 0;
        int[][] newCCSIs = new int[configuration.heatsinkCalculationStepIndicies.length][];
        for(int[] indicies : configuration.heatsinkCalculationStepIndicies){
            int stps = 0;
            int[] newIndicies = new int[indicies.length];
            for(int index : indicies){
                if(blockCount[index]>0){
                    newIndicies[stps] = index;
                    stps++;
                }
            }
            int[] newNewIndicies = new int[stps];
            for(int i = 0; i<stps; i++){
                newNewIndicies[i] = newIndicies[i];
            }
            newCCSIs[steps] = newNewIndicies;
            steps++;
        }
        heatsinkCalculationStepIndicies = new int[steps][];
        System.arraycopy(newCCSIs, 0, heatsinkCalculationStepIndicies, 0, steps);
    }
    public void propogateNeutronFlux(int x, int y, int z, boolean force, boolean initial){
        if(!force&&sourceValid[x][y][z]==0&&neutronFlux[x][y][z]<configuration.blockCriticality[blocks[x][y][z]])return;
        if(!initial&&hadFlux[x][y][z]<configuration.blockCriticality[blocks[x][y][z]])return;
        if(propogated[x][y][z]>0)return;
        propogated[x][y][z]++;
        for(int[] d : directions){
            int flux = 0;
            int length = 0;
            float efficiency = 0;
            for(int i = 1; i<=configuration.neutronReach+1; i++){
                int X = x+d[0]*i;
                int Y = y+d[1]*i;
                int Z = z+d[2]*i;
                if(X<0||Y<0||Z<0||X>=dims[0]||Y>=dims[1]||Z>=dims[2])break;
                int block = blocks[X][Y][Z];
                if(block<0)break;
                if(configuration.blockModerator[block]){
                    flux+=configuration.blockFlux[block];
                    efficiency+=configuration.blockEfficiency[block];
                    length++;
                    continue;
                }
                if(configuration.blockShield[block]){
                    efficiency+=configuration.blockEfficiency[block];
                    length++;
                    continue;
                }
                if(configuration.blockFuelCell[block]){
                    if(length==0)break;
                    neutronFlux[X][Y][Z]+=flux;
                    moderatorLines[X][Y][Z]++;
                    if(flux>0)positionalEfficiency[X][Y][Z]+=efficiency/length;
                    propogateNeutronFlux(X, Y, Z, false, initial);
                    break;
                }
                if(configuration.blockReflector[block]){
                    if(length==0)break;
                    if(length>configuration.neutronReach/2)break;
                    neutronFlux[x][y][z]+=flux*2*configuration.blockReflectivity[block];
                    if(flux>0)positionalEfficiency[x][y][z]+=efficiency/length*configuration.blockEfficiency[block];
                    moderatorLines[x][y][z]++;
                    break;
                }
                if(configuration.blockIrradiator[block]){
                    if(length==0)break;
                    moderatorLines[x][y][z]++;
                    if(flux>0)positionalEfficiency[x][y][z]+=efficiency/length*configuration.blockEfficiency[block];
                    break;
                }
                break;
            }
        }
    }
    public void postFluxCalc(int x, int y, int z){
        if(neutronFlux[x][y][z]<configuration.blockCriticality[blocks[x][y][z]])return;
        for(int[] d : directions){
            int flux = 0;
            int length = 0;
            int[] shieldFluxes = new int[configuration.neutronReach+2];
            int[] toActivate = new int[configuration.neutronReach+2];
            int[] toValidate = new int[configuration.neutronReach+2];
            for(int i = 1; i<=configuration.neutronReach+1; i++){
                int X = x+d[0]*i;
                int Y = y+d[1]*i;
                int Z = z+d[2]*i;
                if(X<0||Y<0||Z<0||X>=dims[0]||Y>=dims[1]||Z>=dims[2])break;
                int block = blocks[X][Y][Z];
                if(block<0)break;
                boolean skip = false;
                if(configuration.blockModerator[block]){
                    length++;
                    flux+=configuration.blockFlux[block];
                    if(i==1)toActivate[i]++;
                    toValidate[i]++;
                    skip = true;
                }
                if(configuration.blockShield[block]){
                    length++;
                    if(i==1)toActivate[i]++;
                    toValidate[i]++;
                    blockActive[X][Y][Z]++;
                    shieldFluxes[i] = flux;
                    skip = true;
                }
                if(skip)continue;
                if(configuration.blockFuelCell[block]&&neutronFlux[X][Y][Z]>=configuration.blockCriticality[block]){
                    if(length==0)break;
                    for(int j = 0; j<shieldFluxes.length; j++){
                        int bx = x+d[0]*j;
                        int by = y+d[1]*j;
                        int bz = z+d[2]*j;
                        neutronFlux[bx][by][bz]+=shieldFluxes[j];
                        blockActive[bx][by][bz]+=toActivate[j];
                        moderatorValid[bx][by][bz]+=toValidate[j];
                    }
                    break;
                }
                if(configuration.blockReflector[block]){
                    if(length==0)break;
                    if(length>configuration.neutronReach/2)break;
                    blockActive[X][Y][Z]++;
                    for(int j = 0; j<shieldFluxes.length; j++){
                        int bx = x+d[0]*j;
                        int by = y+d[1]*j;
                        int bz = z+d[2]*j;
                        neutronFlux[bx][by][bz]+=flux*(1+configuration.blockReflectivity[block]);
                        blockActive[bx][by][bz]+=toActivate[j];
                        moderatorValid[bx][by][bz]+=toValidate[j];
                    }
                    break;
                }
                if(configuration.blockIrradiator[block]){
                    if(length==0)break;
                    neutronFlux[X][Y][Z]+=flux;
                    for(int j = 0; j<shieldFluxes.length; j++){
                        int bx = x+d[0]*j;
                        int by = y+d[1]*j;
                        int bz = z+d[2]*j;
                        neutronFlux[bx][by][bz]+=shieldFluxes[j];
                        blockActive[bx][by][bz]+=toActivate[j];
                        moderatorValid[bx][by][bz]+=toValidate[j];
                    }
                    break;
                }
            }
        }
        propogated[x][y][z]++;
    }
    @Override
    public void calculate(){
        sourceValid = new int[dims[0]][dims[1]][dims[2]];
        propogated = new int[dims[0]][dims[1]][dims[2]];
        neutronFlux = new int[dims[0]][dims[1]][dims[2]];
        hadFlux = new int[dims[0]][dims[1]][dims[2]];
        moderatorLines = new int[dims[0]][dims[1]][dims[2]];
        positionalEfficiency = new float[dims[0]][dims[1]][dims[2]];
        moderatorValid = new int[dims[0]][dims[1]][dims[2]];
        blockActive = new int[dims[0]][dims[1]][dims[2]];
        
        //<editor-fold defaultstate="collapsed" desc="Base flux propogation">
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                        sourceValid[x][y][z]+=findSources(x, y, z, configuration.losTest);
                    }
                }
            }
        }
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]&&sourceValid[x][y][z]>0){
                        propogateNeutronFlux(x, y, z, false, true);
                    }
                }
            }
        }
        //repropogate as many times as neccesary
        int lastActive, nowActive;
        do{
            lastActive = 0;
            for(int x = 0; x<dims[0]; x++){
                for(int y = 0; y<dims[1]; y++){
                    for(int z = 0; z<dims[2]; z++){
                        hadFlux[x][y][z] = neutronFlux[x][y][z];
                        if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                            if(neutronFlux[x][y][z]>=configuration.blockCriticality[blocks[x][y][z]])lastActive++;
                            propogateNeutronFlux(x, y, z, false, false);
                        }
                    }
                }
            }
            //clear everything for repropogation
            propogated = new int[dims[0]][dims[1]][dims[2]];
            neutronFlux = new int[dims[0]][dims[1]][dims[2]];
            moderatorLines = new int[dims[0]][dims[1]][dims[2]];
            positionalEfficiency = new float[dims[0]][dims[1]][dims[2]];
            
            nowActive = 0;
            for(int x = 0; x<dims[0]; x++){
                for(int y = 0; y<dims[1]; y++){
                    for(int z = 0; z<dims[2]; z++){
                        if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                            propogateNeutronFlux(x, y, z, false, false);
                        }
                    }
                }
            }
            
            for(int x = 0; x<dims[0]; x++){
                for(int y = 0; y<dims[1]; y++){
                    for(int z = 0; z<dims[2]; z++){
                        if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                            if(neutronFlux[x][y][z]>=configuration.blockCriticality[blocks[x][y][z]])nowActive++;
                            if(hadFlux[x][y][z]<configuration.blockCriticality[blocks[x][y][z]])neutronFlux[x][y][z] = hadFlux[x][y][z];
                        }
                    }
                }
            }
        }while(nowActive!=lastActive);
        for(int x = 0; x<dims[0]; x++){ //post flux calc
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                        postFluxCalc(x, y, z);
                    }
                }
            }
        }
//</editor-fold>
        //don't care about partial
        //<editor-fold defaultstate="collapsed" desc="Shutdown flux propogation">
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]&&sourceValid[x][y][z]>0){
                        propogateNeutronFlux(x, y, z, blockActive[x][y][z]>0, true);
                    }
                }
            }
        }
        //repropogate as many times as neccesary
        do{
            lastActive = 0;
            for(int x = 0; x<dims[0]; x++){
                for(int y = 0; y<dims[1]; y++){
                    for(int z = 0; z<dims[2]; z++){
                        hadFlux[x][y][z] = neutronFlux[x][y][z];
                        if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                            if(neutronFlux[x][y][z]>=configuration.blockCriticality[blocks[x][y][z]])lastActive++;
                            propogateNeutronFlux(x, y, z, false, false);
                        }
                    }
                }
            }
            //clear everything for repropogation
            propogated = new int[dims[0]][dims[1]][dims[2]];
            neutronFlux = new int[dims[0]][dims[1]][dims[2]];
            moderatorLines = new int[dims[0]][dims[1]][dims[2]];
            positionalEfficiency = new float[dims[0]][dims[1]][dims[2]];
            
            nowActive = 0;
            for(int x = 0; x<dims[0]; x++){
                for(int y = 0; y<dims[1]; y++){
                    for(int z = 0; z<dims[2]; z++){
                        if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                            propogateNeutronFlux(x, y, z, false, false);
                        }
                    }
                }
            }
            
            for(int x = 0; x<dims[0]; x++){
                for(int y = 0; y<dims[1]; y++){
                    for(int z = 0; z<dims[2]; z++){
                        if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                            if(neutronFlux[x][y][z]>=configuration.blockCriticality[blocks[x][y][z]])nowActive++;
                            if(hadFlux[x][y][z]<configuration.blockCriticality[blocks[x][y][z]])neutronFlux[x][y][z] = hadFlux[x][y][z];
                        }
                    }
                }
            }
        }while(nowActive!=lastActive);
        for(int x = 0; x<dims[0]; x++){ //post flux calc
            for(int y = 0; y<dims[1]; y++){
                for(int z = 0; z<dims[2]; z++){
                    if(blocks[x][y][z]>=0&&configuration.blockFuelCell[blocks[x][y][z]]){
                        postFluxCalc(x, y, z);
                    }
                }
            }
        }
//</editor-fold>
    }
    //adjacents
    private int findSources(int x, int y, int z, boolean[] pathTest){
        int count = 0;
        for(int[] direction : directions){
            count+=findSource(x, y, z, direction[0], direction[1], direction[2], pathTest);
        }
        return count;
    }
    private int findSource(int x, int y, int z, int dx, int dy, int dz, boolean[] pathTest){
        for(int dist = 0; dist<=configuration.maxSize; dist++){
            x+=dx;
            y+=dy;
            z+=dz;
            if(x<0||y<0||z<0||x>=dims[0]||y>=dims[1]||z>=dims[2])return 1;
            if(blocks[x][y][z]==-1)continue;
            if(!pathTest[blocks[x][y][z]])return 0;
        }
        return 0;
    }
    
    /*
    private int countAdjacents(int x, int y, int z, boolean[] endTest, boolean[] pathTest, int distance){
        int count = 0;
        for(int[] direction : directions){
            count+=findAdjacent(x, y, z, direction[0], direction[1], direction[2], endTest, pathTest, distance);
        }
        return count;
    }
    private int findAdjacent(int x, int y, int z, int dx, int dy, int dz, boolean[] endTest, boolean[] pathTest, int distance){
        for(int dist = 0; dist<=distance; dist++){
            x+=dx;
            y+=dy;
            z+=dz;
            if(x<0||y<0||z<0||x>=dims[0]||y>=dims[1]||z>=dims[2]||blocks[x][y][z]==-1){
                return 0;//hit casing or air
            }
            if(endTest[blocks[x][y][z]]){
                for(int d = 0; d<=dist; d++){
                    blockValid[x-dx*d][y-dy*d][z-dz*d]++;
                }
                return 1;
            }
            else if(!pathTest[blocks[x][y][z]])return 0;
        }
        return 0;
    }
    */
    @Override
    public void importAndConvert(OverhaulSFR sfr){
        dims[0] = sfr.getInternalWidth();
        dims[1] = sfr.getInternalHeight();
        dims[2] = sfr.getInternalDepth();
        sfr.forEachInternalPosition((x, y, z) -> {
            Block block = sfr.getBlock(x, y, z);
            NCPFElementDefinition definition = block==null?null:block.template.definition;
            NCPFElementDefinition recipe = block.fuel==null?(block.irradiatorRecipe==null?null:block.irradiatorRecipe.definition):block.fuel.definition;
            int b = -1;
            for(int i = 0; i<configuration.blockDefinition.length; i++){
                if(configuration.blockDefinition[i].matches(definition)){
                    if(recipe==null||configuration.blockRecipe[i].matches(recipe))b = i;
                }
            }
            blocks[x-1][y-1][z-1] = b;
        });
        int r = 0;
        for(int i = 0; i<configuration.coolantRecipeDefinition.length; i++){
            if(configuration.coolantRecipeDefinition[i].matches(sfr.coolantRecipe.definition))r = i;
        }
        coolantRecipe = r;
    }
    //tooltip
    //variables
    @Override
    public LiteOverhaulSFR copy(){
        LiteOverhaulSFR copy = new LiteOverhaulSFR(configuration);
        copy.copyFrom(this);
        return copy;
    }
    @Override
    public void copyFrom(LiteMultiblock<OverhaulSFR> other){
        LiteOverhaulSFR sfr = (LiteOverhaulSFR)other;
        coolantRecipe = sfr.coolantRecipe;
        dims[0] = sfr.dims[0];;
        dims[1] = sfr.dims[1];
        dims[2] = sfr.dims[2];
        for(int x = 0; x<dims[0]; x++){
            for(int y = 0; y<dims[1]; y++){
                System.arraycopy(sfr.blocks[x][y], 0, blocks[x][y], 0, dims[2]);
            }
        }
    }
    //copyVarsFrom
    @Override
    public OverhaulSFR export(NCPFConfigurationContainer configg){
        OverhaulSFRConfiguration config = configg.getConfiguration(OverhaulSFRConfiguration::new);
        CoolantRecipe coolantRecipe = null;
        for(CoolantRecipe r : config.coolantRecipes){
            if(r.definition.matches(configuration.coolantRecipeDefinition[this.coolantRecipe])){
                coolantRecipe = r;
                break;
            }
        }
        calculate();
        OverhaulSFR sfr = new OverhaulSFR(configg, dims[0], dims[1], dims[2], coolantRecipe);
        sfr.forEachInternalPosition((x, y, z) -> {
            int block = blocks[x-1][y-1][z-1];
            if(blockValid[x-1][y-1][z-1]+blockActive[x-1][y-1][z-1]+blockEfficiency[x-1][y-1][z-1]<=0)block = -1;
            Block bl = null;
            if(block>=0){
                for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement b : config.blocks){
                    if(b.definition.matches(configuration.blockDefinition[block])){
                        bl = new Block(configg, x, y, z, b);
                        NCPFElementDefinition recip = configuration.blockRecipe[block];
                        if(recip!=null){
                            for(Fuel fuel : b.fuels){
                                if(fuel.definition.matches(recip))bl.fuel = fuel;
                            }
                            for(IrradiatorRecipe recipe : b.irradiatorRecipes){
                                if(recipe.definition.matches(recip))bl.irradiatorRecipe = recipe;
                            }
                        }
                        break;
                    }
                }
            }
            sfr.setBlock(x, y, z, bl);
        });
        if(Core.autoBuildCasing)sfr.buildDefaultCasing();
        return sfr;
    }
    @Override
    public int getDimension(int id){
        return dims[id];
    }
    @Override
    public Image getBlockTexture(int x, int y, int z){
        if(blockValid==null||blockActive==null||blockEfficiency==null)return null;
        if(blockValid[x][y][z]+blockActive[x][y][z]+blockEfficiency[x][y][z]<1)return null;
        int block = blocks[x][y][z];
        return block>=0?configuration.blockTexture[block]:null;
    }
    @Override
    public float getCubeBounds(int x, int y, int z, int index){
        if(index<3)return 0;
        return 1;
    }
    //generators
    @Override
    public void clear(){
        for(int x = 0; x<configuration.maxSize; x++){
            for(int y = 0; y<configuration.maxSize; y++){
                for(int z = 0; z<configuration.maxSize; z++){
                    blocks[x][y][z] = -1;
                }
            }
        }
        calculate();//fix vars
    }
}
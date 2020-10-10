package multiblock.overhaul.turbine;
import generator.Priority;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import multiblock.Multiblock;
import multiblock.PartCount;
import multiblock.configuration.Configuration;
import multiblock.configuration.TextureManager;
import multiblock.configuration.overhaul.turbine.Blade;
import multiblock.configuration.overhaul.turbine.Coil;
import multiblock.configuration.overhaul.turbine.Recipe;
import multiblock.ppe.ClearInvalid;
import multiblock.ppe.PostProcessingEffect;
import multiblock.symmetry.CoilSymmetry;
import multiblock.symmetry.Symmetry;
import org.lwjgl.opengl.GL11;
import planner.file.NCPFFile;
import planner.menu.component.MenuComponentMinimaList;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigNumberList;
public class OverhaulTurbine extends Multiblock<Block>{
    static BufferedImage shaftTexture = TextureManager.getImage("overhaul/turbine/shaft");
    public int bearingDiameter;
    public Recipe recipe;
    public boolean bladesValid;
    private int bladeCount;//for invalid metering
    public float rotorEfficiency;
    public int maxInput;
    public int maxUnsafeInput;
    public double throughputEfficiency;
    private float coilEfficiency;
    private double totalEfficiency;
    private double totalFluidEfficiency;
    private long totalOutput,safeOutput,unsafeOutput;
    public ArrayList<Multiblock> inputs = new ArrayList<>();
    public double[] idealExpansion;
    public double[] actualExpansion;
    public OverhaulTurbine(){
        this(3, 3, 1, null);
        updateBlockLocations();
    }
    public OverhaulTurbine(int diameter, int length, int bearingDiameter, Recipe recipe){
        super(diameter, diameter, length+2);
        if(bearingDiameter%2!=diameter%2){
            throw new IllegalArgumentException("Bearing size is "+(bearingDiameter%2==0?"even":"odd")+"!, but turbine diameter is not!");
        }
        this.bearingDiameter = bearingDiameter;
        this.recipe = recipe==null?getConfiguration().overhaul.turbine.allRecipes.get(0):recipe;
        updateBlockLocations();
    }
    @Override
    public String getDefinitionName(){
        return "Overhaul Turbine";
    }
    @Override
    public Multiblock<Block> newInstance(Configuration configuration){
        OverhaulTurbine turbine = new OverhaulTurbine();
        turbine.setConfiguration(configuration);
        return turbine;
    }
    public void setCoilExact(int x, int y, int z, multiblock.Block exact){
        if(z==1)z = getZ()-1;
        if(exact!=null)exact.z = z;
        setBlockExact(x, y, z, exact);
    }
    public void setBladeExact(int z, multiblock.Block exact){
        setBlockExact(getX()/2, 0, z, exact);
    }
    @Override
    public void setBlock(int x, int y, int z, multiblock.Block block){
        int minD = getX()/2-bearingDiameter/2;
        int maxD = getX()-minD-1;
        if(x>=minD&&y>=minD&&x<=maxD&&y<=maxD){
            if(block==null)return;
            Block tb = (Block)block;
            if(z==0||z==getZ()-1){
                if(!tb.isBearing())return;
            }else{
                if(!tb.isCasing())return;
            }
        }else{
            if(block!=null){
                Block tb = (Block)block;
                if(z==0||z==getZ()-1){
                    if(!tb.isConnector()&&!tb.isCoil())return;
                }else{
                    if(!tb.isBlade())return;
                    multiblock.Block b = block.copy();
                    for(int X = 0; X<getX(); X++){
                        for(int Y = 0; Y<getY(); Y++){
                            if(X<minD||Y<minD||X>maxD||Y>maxD){
                                if(X<minD&&Y<minD||X<minD&&Y>maxD||X>maxD&&Y<minD||X>maxD&&Y>maxD)continue;
                                super.setBlockExact(X, Y, z, b);
                            }
                        }
                    }
                    return;
                }
            }else{
                if(z!=0&&z!=getZ()-1){
                    for(int X = 0; X<getX(); X++){
                        for(int Y = 0; Y<getY(); Y++){
                            if(X<minD||Y<minD||X>maxD||Y>maxD){
                                if(X<minD&&Y<minD||X<minD&&Y>maxD||X>maxD&&Y<minD||X>maxD&&Y>maxD)continue;
                                super.setBlockExact(X, Y, z, null);
                            }
                        }
                    }
                }
            }
        }
        super.setBlock(x, y, z, block);
    }
    @Override
    public void setBlockExact(int x, int y, int z, multiblock.Block exact){
        int minD = getX()/2-bearingDiameter/2;
        int maxD = getX()-minD-1;
        if(x>=minD&&y>=minD&&x<=maxD&&y<=maxD){
            if(exact==null)return;
            Block tb = (Block)exact;
            if(z==0||z==getZ()-1){
                if(!tb.isBearing())return;
            }else{
                if(!tb.isCasing())return;
            }
        }else{
            if(exact!=null){
                Block tb = (Block)exact;
                if(z==0||z==getZ()-1){
                    if(!tb.isConnector()&&!tb.isCoil())return;
                }else{
                    if(!tb.isBlade())return;
                    for(int X = 0; X<getX(); X++){
                        for(int Y = 0; Y<getY(); Y++){
                            if(X<minD||Y<minD||X>maxD||Y>maxD){
                                if(X<minD&&Y<minD||X<minD&&Y>maxD||X>maxD&&Y<minD||X>maxD&&Y>maxD)continue;
                                super.setBlockExact(X, Y, z, exact);
                            }
                        }
                    }
                    return;
                }
            }else{
                if(z!=0&&z!=getZ()-1){
                    for(int X = 0; X<getX(); X++){
                        for(int Y = 0; Y<getY(); Y++){
                            if(X<minD||Y<minD||X>maxD||Y>maxD){
                                if(X<minD&&Y<minD||X<minD&&Y>maxD||X>maxD&&Y<minD||X>maxD&&Y>maxD)continue;
                                super.setBlockExact(X, Y, z, exact);
                            }
                        }
                    }
                }
            }
        }
        super.setBlockExact(x, y, z, exact);
    }
    @Override
    public Multiblock<Block> newInstance(int x, int y, int z){
        return new OverhaulTurbine(x, z-2, x%2==0?2:1, null);
    }
    @Override
    public void getAvailableBlocks(List<Block> blocks){
        if(getConfiguration()==null||getConfiguration().overhaul==null||getConfiguration().overhaul.turbine==null)return;
        for(Blade blade : getConfiguration().overhaul.turbine.allBlades){
            blocks.add(new Block(-1, blade));
        }
        for(Coil coil : getConfiguration().overhaul.turbine.allCoils){
            blocks.add(new Block(-1, -1, -1, coil));
        }
    }
    @Override
    public int getDisplayZ(){
        return getZ()-2;
    }
    @Override
    public int getMinX(){
        return getConfiguration().overhaul.turbine.minWidth;
    }
    @Override
    public int getMinY(){
        return getConfiguration().overhaul.turbine.minWidth;
    }
    @Override
    public int getMinZ(){
        return getConfiguration().overhaul.turbine.minLength+2;
    }
    @Override
    public int getMaxX(){
        return getConfiguration().overhaul.turbine.maxSize;
    }
    @Override
    public int getMaxY(){
        return getConfiguration().overhaul.turbine.maxSize;
    }
    @Override
    public int getMaxZ(){
        return getConfiguration().overhaul.turbine.maxSize+2;
    }
    @Override
    public void expandRight(int i){
        if(getX()+i>getMaxX())return;
        if(getY()+i>getMaxY())return;
        bearingDiameter++;
        multiblock.Block[][][] blks = new multiblock.Block[getX()+i][getY()+i][getZ()];
//        for(int x = 0; x<blocks.length; x++){
//            for(int y = 0; y<blocks[x].length; y++){
//                for(int z = 0; z<blocks[x][y].length; z++){
//                    blks[x][y][z] = blocks[x][y][z];
//                }
//            }
//        }
        blocks = blks;
        updateBlockLocations();
    }
    @Override
    public void expandLeft(int i){
        if(getX()+i>getMaxX())return;
        if(getY()+i>getMaxY())return;
        bearingDiameter++;
        multiblock.Block[][][] blks = new multiblock.Block[getX()+i][getY()+i][getZ()];
//        for(int x = 0; x<blocks.length; x++){
//            for(int y = 0; y<blocks[x].length; y++){
//                for(int z = 0; z<blocks[x][y].length; z++){
//                    blks[x+i][y+i][z] = blocks[x][y][z];
//                }
//            }
//        }
        blocks = blks;
        updateBlockLocations();
    }
    @Override
    public void expandUp(int i){
        expandRight(i);
    }
    @Override
    public void exandDown(int i){
        expandLeft(i);
    }
    @Override
    public void expandToward(int i){
        insertZ(1);
    }
    @Override
    public void expandAway(int i){
        insertZ(1);
    }
    @Override
    public void deleteX(int X){
        if(getX()<=getMinX())return;
        multiblock.Block[][][] blks = new multiblock.Block[getX()-1][getY()-1][getZ()];
        bearingDiameter--;
//        for(int x = 0; x<blks.length; x++){
//            for(int y = 0; y<blks[x].length; y++){
//                for(int z = 0; z<blks[x][y].length; z++){
//                    blks[x][y][z] = blocks[(x>=X?1:0)+x][y][z];
//                }
//            }
//        }
        blocks = blks;
        updateBlockLocations();
    }
    @Override
    public void deleteY(int Y){
        if(getY()<=getMinY())return;
        multiblock.Block[][][] blks = new multiblock.Block[getX()-1][getY()-1][getZ()];
        bearingDiameter--;
//        for(int x = 0; x<blks.length; x++){
//            for(int y = 0; y<blks[x].length; y++){
//                for(int z = 0; z<blks[x][y].length; z++){
//                    blks[x][y][z] = blocks[x][(y>=Y?1:0)+y][z];
//                }
//            }
//        }
        blocks = blks;
        updateBlockLocations();
    }
    @Override
    public void deleteZ(int Z){
        if(Z==0||Z==getZ()-1)return;
        if(getZ()<=getMinZ())return;
        multiblock.Block[][][] blks = new multiblock.Block[getX()][getY()][getZ()-1];
        for(int x = 0; x<blks.length; x++){
            for(int y = 0; y<blks[x].length; y++){
                for(int z = 0; z<blks[x][y].length; z++){
                    blks[x][y][z] = blocks[x][y][(z>=Z?1:0)+z];
                }
            }
        }
        blocks = blks;
        updateBlockLocations();
    }
    @Override
    public void insertX(int X){
        expandRight(1);
    }
    @Override
    public void insertY(int Y){
        expandUp(1);
    }
    @Override
    public void insertZ(int Z){
        Z = 1;
        if(getZ()>=getMaxZ())return;
        multiblock.Block[][][] blks = new multiblock.Block[getX()][getY()][getZ()+1];
        for(int x = 0; x<blocks.length; x++){
            for(int y = 0; y<blocks[x].length; y++){
                for(int z = 0; z<blocks[x][y].length; z++){
                    blks[x][y][(z>=Z?1:0)+z] = blocks[x][y][z];
                }
            }
        }
        blocks = blks;
        updateBlockLocations();
    }
    private void updateBlockLocations(){
        if(bearingDiameter==getX())bearingDiameter-=2;
        if(bearingDiameter<=0)bearingDiameter+=2;
        Block shaft = new Block(0, null);//internal casing? :O
        Coil bearing = null;
        for(Coil coil : getConfiguration().overhaul.turbine.allCoils){
            if(coil.bearing)bearing = coil;
        }
        int minD = getX()/2-bearingDiameter/2;
        int maxD = getX()-minD-1;
        Block[] blades = new Block[getZ()-2];
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    Block b = getBlock(x, y, z);
                    if(b!=null){
                        if(b.isBearing()||b.isCasing())setBlockExact(x, y, z, null);
                    }
                }
            }
        }
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    if(x>=minD&&x<=maxD&&y>=minD&&y<=maxD){
                        if(z==0||z==getZ()-1){
                            setBlockExact(x, y, z, new Block(x, y, z, bearing));
                        }else{
                            setBlockExact(x, y, z, shaft);
                        }
                    }else{
                        if(z==0||z==getZ()-1){
                            if(blocks[x][y][z]!=null){
                                blocks[x][y][z].x = x;
                                blocks[x][y][z].y = y;
                                blocks[x][y][z].z = z;
                            }
                        }else{
                            Block b = getBlock(x, y, z);
                            if(b!=null&&blades[z-1]==null)blades[z-1] = b;
                        }
                    }
                }
            }
        }
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    if(!(x>=minD&&x<=maxD&&y>=minD&&y<=maxD)){
                        if(z!=0&&z!=getZ()-1){
                            setBlockExact(x, y, z, blades[z-1]);
                        }
                    }
                }
            }
        }
        history.clear();
        future.clear();
    }
    @Override
    public void calculate(List<Block> blocks){
        bladesValid = true;
        bladeCount = 0;
        Block[] blades = new Block[getZ()-2];
        for(int z = 1; z<getZ()-1; z++){
            Block b = getBlock(getX()/2, 0, z);
            if(b==null||b.blade==null)bladesValid = false;
            blades[z-1] = b;
            bladeCount++;
        }
        if(bladesValid){
            idealExpansion = new double[blades.length];
            actualExpansion = new double[blades.length];
            double expansionSoFar = 1;
            rotorEfficiency = 0;
            float minBladeExpansion = Float.MAX_VALUE;
            float maxBladeExpansion = 0;
            float minStatorExpansion = 1;
            int numBlades = 0;
            int numberOfBlades = 0;
            for(int i = 0; i<blades.length; i++){
                if(blades[i].blade.stator){
                    minStatorExpansion = Math.min(blades[i].blade.expansion, minStatorExpansion);
                }else{
                    numberOfBlades++;
                    numBlades+=bearingDiameter*4*(getX()/2-bearingDiameter/2);
                    minBladeExpansion = Math.min(blades[i].blade.expansion, minBladeExpansion);
                    maxBladeExpansion = Math.max(blades[i].blade.expansion, maxBladeExpansion);
                }
                idealExpansion[i] = Math.pow(recipe.coefficient, (i+.5f)/blades.length);
                actualExpansion[i] = expansionSoFar*Math.sqrt(blades[i].blade.expansion);
                expansionSoFar*=blades[i].blade.expansion;
                rotorEfficiency+=blades[i].blade.efficiency*Math.min(actualExpansion[i]/idealExpansion[i], idealExpansion[i]/actualExpansion[i]);
            }
            rotorEfficiency/=numberOfBlades;
            maxInput = numBlades*getConfiguration().overhaul.turbine.fluidPerBlade;
            maxUnsafeInput = maxInput*2;
            int effectiveMaxLength;
            if(minBladeExpansion<=1||minStatorExpansion>=1d){
                effectiveMaxLength = getConfiguration().overhaul.turbine.maxSize;
            }else{
                effectiveMaxLength = (int)Math.ceil(Math.max(getConfiguration().overhaul.turbine.minLength, Math.min(getConfiguration().overhaul.turbine.maxSize, (Math.log(recipe.coefficient)-getConfiguration().overhaul.turbine.maxSize*Math.log(minStatorExpansion))/(Math.log(minBladeExpansion)-Math.log(minStatorExpansion)))));
            }
            int bladeArea = bearingDiameter*4*(getX()/2-bearingDiameter/2);
            double rate = Math.min(getInputRate(), maxInput);
            double lengthBonus = rate/(getConfiguration().overhaul.turbine.fluidPerBlade*bladeArea*effectiveMaxLength);
            double areaBonus = Math.sqrt(2*rate/(getConfiguration().overhaul.turbine.fluidPerBlade*(getZ()-2)*getConfiguration().overhaul.turbine.maxSize*effectiveMaxLength));
            double effectiveMinLength = recipe.coefficient<=1||maxBladeExpansion<=1?getConfiguration().overhaul.turbine.maxSize:Math.ceil(Math.log(recipe.coefficient)/Math.log(maxBladeExpansion));
            int minBladeArea = ((getConfiguration().overhaul.turbine.minWidth-1)*2);
            double absoluteLeniency = effectiveMinLength*minBladeArea*getConfiguration().overhaul.turbine.fluidPerBlade;
            double throughputRatio = maxInput==0?1:Math.min(1, (getInputRate()+absoluteLeniency)/maxInput);
            double throughputEfficiencyMult = throughputRatio>=getConfiguration().overhaul.turbine.throughputEfficiencyLeniencyThreshold?1:(1-getConfiguration().overhaul.turbine.throughputEfficiencyLeniencyMult)*Math.sin(throughputRatio*Math.PI/(2*getConfiguration().overhaul.turbine.throughputEfficiencyLeniencyThreshold))+getConfiguration().overhaul.turbine.throughputEfficiencyLeniencyMult;
            throughputEfficiency = (1+getConfiguration().overhaul.turbine.powerBonus*Math.pow(lengthBonus*areaBonus, 2/3d))*throughputEfficiencyMult;
        }
        boolean somethingChanged;
        do{
            somethingChanged = false;
            for(Block block : blocks){
                if(block.calculateCoil(this))somethingChanged = true;
            }
        }while(somethingChanged);
        float inputEff = 0;
        float outputEff = 0;
        int inputCoils = 0;
        int outputCoils = 0;
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                Block in = getBlock(x, y, 0);
                if(in!=null&&in.isCoil()&&in.isActive()){
                    inputEff+=in.coil.efficiency;
                    inputCoils++;
                }
                Block out = getBlock(x, y, getZ()-1);
                if(out!=null&&out.isCoil()&&out.isActive()){
                    outputEff+=out.coil.efficiency;
                    outputCoils++;
                }
            }
        }
        int bearings = bearingDiameter*bearingDiameter;
        inputEff/=Math.max(inputCoils, bearings/2);
        outputEff/=Math.max(outputCoils, bearings/2);
        if(Float.isNaN(inputEff))inputEff = 0;
        if(Float.isNaN(outputEff))outputEff = 0;
        coilEfficiency = (inputEff+outputEff)/2;
        totalEfficiency = coilEfficiency*rotorEfficiency*throughputEfficiency;//*getConfiguration().overhaul.turbine.throughputEfficiencyLeniency;
        totalFluidEfficiency = totalEfficiency*recipe.power;
        totalOutput = (long)(totalFluidEfficiency*getInputRate());
        safeOutput = (long)(totalFluidEfficiency*maxInput);
        unsafeOutput = (long)(totalFluidEfficiency*maxUnsafeInput);
    }
    @Override
    protected Block getCasing(int x, int y, int z){
        return Block.casing(x, y, z);
    }
    @Override
    public String getTooltip(){
        if(bladesValid){
            return "Total output: "+totalOutput+" RF/t\n"
                 + "Input: "+getInputRate()+"/"+maxInput+" mb/t\n"
                 + "Power Efficiency: "+round(totalFluidEfficiency, 2)+" RF/mb\n"
                 + "Total Efficiency: "+percent(totalEfficiency, 2)+"\n"
                 + "Rotor Efficiency: "+percent(rotorEfficiency, 2)+"\n"
                 + "Coil Efficiency: "+percent(coilEfficiency, 2)+"\n"
                 + "Throughput Efficiency: "+percent(throughputEfficiency, 2);
        }else{
            return "<Rotor Invalid>\n"
                 + "Input: "+getInputRate()+"/"+maxInput+" mb/t\n"
                 + "Coil Efficiency: "+percent(coilEfficiency, 2);
        }
    }
    @Override
    protected String getExtraBotTooltip(){
        return getTooltip();
    }
    @Override
    public int getMultiblockID(){
        return 3;
    }
    @Override
    protected void save(NCPFFile ncpf, Configuration configuration, Config config){
        ConfigNumberList inputs = new ConfigNumberList();
        for(Multiblock m : this.inputs){
            if(ncpf.multiblocks.contains(m))inputs.add(ncpf.multiblocks.indexOf(m));
        }
        if(inputs.size()!=0)config.set("inputs", inputs);
        ConfigNumberList size = new ConfigNumberList();
        size.add(getX());
        size.add(getZ()-2);
        size.add(bearingDiameter);
        config.set("size", size);
        ConfigNumberList blades = new ConfigNumberList();
        for(int z = 1; z<getZ()-1; z++){
            Block block = getBlock(getX()/2, 0, z);
            if(block==null)blades.add(0);
            else blades.add(configuration.overhaul.turbine.allBlades.indexOf(block.blade)+1);
        }
        config.set("blades", blades);
        ConfigNumberList coils = new ConfigNumberList();
        for(int z = 0; z<2; z++){
            if(z==1)z = getZ()-1;
            for(int x = 0; x<getX(); x++){
                for(int y = 0; y<getY(); y++){
                    Block block = getBlock(x, y, z);
                    if(block==null)coils.add(0);
                    else coils.add(configuration.overhaul.turbine.allCoils.indexOf(block.coil)+1);
                }
            }
        }
        config.set("coils", coils);
        config.set("recipe", (byte)configuration.overhaul.turbine.allRecipes.indexOf(recipe));
    }
    @Override
    public void convertTo(Configuration to){
        if(to.overhaul==null||to.overhaul.turbine==null)return;
        for(Block block : getBlocks()){
            block.coil = to.overhaul.turbine.convert(block.coil);
            block.blade = to.overhaul.turbine.convert(block.blade);
        }
        recipe = to.overhaul.turbine.convert(recipe);
        setConfiguration(to);
    }
    @Override
    public boolean validate(){
        return false;
    }
    @Override
    public void addGeneratorSettings(MenuComponentMinimaList multiblockSettings){}
    @Override
    public void getGenerationPriorities(ArrayList<Priority> priorities){
        priorities.add(new Priority<OverhaulTurbine>("Valid Rotor", true){
            @Override
            protected double doCompare(OverhaulTurbine main, OverhaulTurbine other){
                return main.bladeCount-other.bladeCount;
            }
        });
        priorities.add(new Priority<OverhaulTurbine>("RF per mb", true){
            @Override
            protected double doCompare(OverhaulTurbine main, OverhaulTurbine other){
                return main.totalFluidEfficiency-other.totalFluidEfficiency;
            }
        });
        priorities.add(new Priority<OverhaulTurbine>("Total Efficiency", true){
            @Override
            protected double doCompare(OverhaulTurbine main, OverhaulTurbine other){
                return main.totalEfficiency-other.totalEfficiency;
            }
        });
        priorities.add(new Priority<OverhaulTurbine>("Rotor Efficiency", true){
            @Override
            protected double doCompare(OverhaulTurbine main, OverhaulTurbine other){
                return main.rotorEfficiency-other.rotorEfficiency;
            }
        });
        priorities.add(new Priority<OverhaulTurbine>("Coil Efficiency", true){
            @Override
            protected double doCompare(OverhaulTurbine main, OverhaulTurbine other){
                return main.coilEfficiency-other.coilEfficiency;
            }
        });
    }
    @Override
    public void getGenerationPriorityPresets(ArrayList<Priority> priorities, ArrayList<Priority.Preset> presets){
        presets.add(new Priority.Preset("RF per mb", priorities.get(0), priorities.get(1)));
        presets.add(new Priority.Preset("Efficiency", priorities.get(0), priorities.get(2)));
        presets.add(new Priority.Preset("Rotor Efficiency", priorities.get(0), priorities.get(3)));
        presets.add(new Priority.Preset("Coil Efficiency", priorities.get(0), priorities.get(4)));
    }
    @Override
    public void getSymmetries(ArrayList<Symmetry> symmetries){
        symmetries.add(CoilSymmetry.X);
        symmetries.add(CoilSymmetry.Y);
        symmetries.add(CoilSymmetry.Z);
    }
    @Override
    public void getPostProcessingEffects(ArrayList<PostProcessingEffect> postProcessingEffects){
        postProcessingEffects.add(new ClearInvalid());
    }
    @Override
    public void clearData(List<Block> blocks){
        super.clearData(blocks);
        bladesValid = false;
        bladeCount = 0;
        throughputEfficiency = totalEfficiency = totalFluidEfficiency = rotorEfficiency = coilEfficiency = 0;
        totalOutput = safeOutput = unsafeOutput = maxInput = maxUnsafeInput = 0;
    }
    @Override
    public boolean exists(){
        return getConfiguration().overhaul!=null&&getConfiguration().overhaul.turbine!=null;
    }
    @Override
    public OverhaulTurbine blankCopy(){
        return new OverhaulTurbine(getX(), getZ()-2, bearingDiameter, recipe);
    }
    @Override
    public OverhaulTurbine copy(){
        OverhaulTurbine copy = blankCopy();
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                for(int z = 0; z<getZ(); z++){
                    Block get = getBlock(x, y, z);
                    if(get!=null)copy.setBlockExact(x, y, z, get.copy());
                }
            }
        }
        copy.bladesValid = bladesValid;
        copy.bladeCount = bladeCount;
        copy.throughputEfficiency = throughputEfficiency;
        copy.totalEfficiency = totalEfficiency;
        copy.totalFluidEfficiency = totalFluidEfficiency;
        copy.rotorEfficiency = rotorEfficiency;
        copy.coilEfficiency = coilEfficiency;
        copy.totalOutput = totalOutput;
        copy.safeOutput = safeOutput;
        copy.unsafeOutput = unsafeOutput;
        copy.maxInput = maxInput;
        copy.maxUnsafeInput = maxUnsafeInput;
        return copy;
    }
    @Override
    protected int doCount(Object o){
        throw new IllegalArgumentException("Cannot count "+o.getClass().getName()+" in "+getDefinitionName()+"!");
    }
    @Override
    public String getGeneralName(){
        return "Turbine";
    }
    @Override
    protected boolean isCompatible(Multiblock<Block> other){
        return ((OverhaulTurbine)other).recipe==recipe;
    }
    private int getInputRate(){
        if(inputs.isEmpty())return maxInput;
        int input = 0;
        for(Multiblock m : inputs){
            input+=(double)m.getFluidOutputs().get(recipe.input);
        }
        return input;
    }
    @Override
    protected void getFluidOutputs(HashMap<String, Double> outputs){
        outputs.put(recipe.output, getInputRate()*recipe.coefficient);
    }
    @Override
    protected void getMainParts(ArrayList<PartCount> parts){
        HashMap<Block, Integer> blocks = new HashMap<>();
        FOR:for(Block block : getBlocks(true)){
            for(Block b : blocks.keySet()){
                if(b.isEqual(block)){
                    blocks.put(b, blocks.get(b)+1);
                    continue FOR;
                }
            }
            blocks.put(block, 1);
        }
        for(Block block : blocks.keySet()){
            parts.add(new PartCount(block.getTexture(), block.getName(), blocks.get(block)));
        }
    }
    @Override
    protected void getExtraParts(ArrayList<PartCount> parts){
        int edgeCasings = 0;
        for(int x = 0; x<getX(); x++){
            for(int y = 0; y<getY(); y++){
                if(getBlock(x, y, 0)==null)edgeCasings++;
                if(getBlock(x, y, getZ()-1)==null)edgeCasings++;
            }
        }
        parts.add(new PartCount(null, "Casing", (getX()+2)*(getZ()+2)*2+(getZ()+2)*getY()*2+edgeCasings-1));
    }
    @Override
    protected void drawCube(Block block, boolean inOrder){
        int x = block.x;
        int y = block.y;
        int z = block.z;
        double minX = x;
        double minY = y;
        double minZ = z;
        double maxX = x+1;
        double maxY = y+1;
        double maxZ = z+1;
        if(block.isBlade()){//2 pixels thick, 12 pixels wide
            if(block.blade.stator){
                if(x>getX()/2+bearingDiameter/2||x<getX()/2-bearingDiameter/2){//side
                    minY+=7/16d;
                    maxY-=7/16d;
                    minZ+=2/16d;
                    maxZ-=2/16d;
                }else{//top
                    minX+=7/16d;
                    maxX-=7/16d;
                    minZ+=2/16d;
                    maxZ-=2/16d;
                }
            }else{//blade
                if(x>getX()/2+bearingDiameter/2||x<getX()/2-bearingDiameter/2){//side
                    minY+=2/16d;
                    maxY-=2/16d;
                    minZ+=7/16d;
                    maxZ-=7/16d;
                }else{//top
                    minX+=2/16d;
                    maxX-=2/16d;
                    minZ+=7/16d;
                    maxZ-=7/16d;
                }
            }
        }
        //xy +z
        if(!inOrder&&(z==getZ()-1||getBlock(x, y, z+1)==null||!getBlock(x, y, z+1).isFullBlock()||!block.isFullBlock())){
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(minX, minY, maxZ);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(maxX, minY, maxZ);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(maxX, maxY, maxZ);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(minX, maxY, maxZ);
        }
        //xy -z
        if(z==0||getBlock(x,y,z-1)==null||!getBlock(x,y,z-1).isFullBlock()||!block.isFullBlock()){
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(minX, minY, minZ);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(minX, maxY, minZ);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(maxX, maxY, minZ);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(maxX, minY, minZ);
        }
        //xz +y
        if(!inOrder&&(y==getY()-1||getBlock(x, y+1, z)==null||!getBlock(x, y+1, z).isFullBlock()||!block.isFullBlock())){
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(minX, maxY, minZ);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(minX, maxY, maxZ);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(maxX, maxY, maxZ);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(maxX, maxY, minZ);
        }
        //xz -y
        if(y==0||getBlock(x, y-1, z)==null||!getBlock(x, y-1, z).isFullBlock()||!block.isFullBlock()){
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(minX, minY, minZ);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(maxX, minY, minZ);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(maxX, minY, maxZ);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(minX, minY, maxZ);
        }
        //yz +x
        if(minX==getX()-1||getBlock(x+1, y, z)==null||!getBlock(x+1, y, z).isFullBlock()||!block.isFullBlock()){
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(maxX, minY, minZ);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(maxX, maxY, minZ);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(maxX, maxY, maxZ);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(maxX, minY, maxZ);
        }
        //yz -x
        if(!inOrder&&(minX==0||getBlock(x-1, y, z)==null||!getBlock(x-1, y, z).isFullBlock()||!block.isFullBlock())){
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(minX, minY, minZ);
            GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(minX, minY, maxZ);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(minX, maxY, maxZ);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(minX, maxY, minZ);
        }
    }
    @Override
    public String getDescriptionTooltip(){
        return "Overhaul Turbines are Turbines in NuclearCraft: Overhauled";
    }
}
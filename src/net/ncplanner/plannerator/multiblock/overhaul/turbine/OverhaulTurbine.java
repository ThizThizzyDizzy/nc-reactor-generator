package net.ncplanner.plannerator.multiblock.overhaul.turbine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.CuboidalMultiblock;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.PartCount;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.multiblock.editor.action.SetblockAction;
import net.ncplanner.plannerator.multiblock.editor.action.SetblocksAction;
import net.ncplanner.plannerator.multiblock.editor.decal.BlockInvalidDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.BlockValidDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.MissingBladeDecal;
import net.ncplanner.plannerator.multiblock.editor.decal.MissingCasingDecal;
import net.ncplanner.plannerator.multiblock.generator.Priority;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.Queue;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestion;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestor;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.MenuEdit;
import net.ncplanner.plannerator.planner.gui.menu.component.editor.MenuComponentEditorGrid;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulTurbineConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.TurbineRecipe;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulTurbineDesign;
@RegisterWith(module = OverhaulModule.class)
public class OverhaulTurbine extends CuboidalMultiblock<Block>{
    public TurbineRecipe recipe;
    public boolean rotorValid;
    private int bladeCount;//for invalid metering
    public float rotorEfficiency;
    public int maxInput;
    public int maxUnsafeInput;
    public double throughputEfficiency;
    public double idealityMultiplier;
    private float coilEfficiency;
    private double totalEfficiency;
    private double totalFluidEfficiency;
    private long totalOutput, safeOutput, unsafeOutput;
    public ArrayList<Multiblock> inputs = new ArrayList<>();
    public double[] idealExpansion;
    public double[] actualExpansion;
    private boolean hasInlet, hasOutlet;
    public int bearingDiameter = 0;
    private BlockElement[] blades;
    private boolean[] bladesComplete;
    private int calcStep = 0;
    private int calcSubstep = 0;
    private int numControllers;
    private int missingCasings;
    private Task calcCasing;
    private Task calcBearing;
    private Task calcBlades;
    private Task calcRotor;
    private Task calcCoils;
    private Task calcStats;
    public OverhaulTurbine(){
        this(null);
    }
    public OverhaulTurbine(NCPFConfigurationContainer configuration){
        this(configuration, 3, 3, null);
    }
    public OverhaulTurbine(NCPFConfigurationContainer configuration, int diameter, int length, TurbineRecipe recipe){
        super(configuration, diameter, diameter, length);
        this.recipe = recipe==null?(exists()?getSpecificConfiguration().recipes.get(0):null):recipe;
    }
    @Override
    public OverhaulTurbineConfiguration getSpecificConfiguration(){
        NCPFConfigurationContainer conf = getConfiguration();
        if(conf==null)return null;
        return conf.getConfiguration(OverhaulTurbineConfiguration::new);
    }
    @Override
    public String getDefinitionName(){
        return "Overhaul Turbine";
    }
    @Override
    public Multiblock<Block> newInstance(NCPFConfigurationContainer configuration){
        return new OverhaulTurbine(configuration);
    }
    @Deprecated // Use multiblock symmetries instead
    public void setBearing(int bearingSize){
        int bearingMax = getExternalWidth()/2+bearingSize/2;
        int bearingMin = getExternalWidth()/2-bearingSize/2;
        BlockElement bearing = null;
        BlockElement shaft = null;
        for(BlockElement block : getSpecificConfiguration().blocks){
            if(block.shaft!=null&&shaft==null)shaft = block;
            if(block.bearing!=null&&bearing==null)bearing = block;
        }
        for(int z = 0; z<getExternalDepth(); z++){
            for(int x = bearingMin; x<=bearingMax; x++){
                for(int y = bearingMin; y<=bearingMax; y++){
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockElement block = shaft;
                    if(z==0||z==getExternalDepth()-1)block = bearing;
                    if(block!=null)setBlock(pos, new Block(getConfiguration(), pos, block));
                }
            }
        }
    }
    @Deprecated // Use multiblock symmetries instead
    public void setBlade(int bearingSize, int z, BlockElement block){
        int bearingMax = getExternalWidth()/2+bearingSize/2;
        int bearingMin = getExternalWidth()/2-bearingSize/2;
        for(int x = 1; x<=getInternalWidth(); x++){
            for(int y = 1; y<=getInternalHeight(); y++){
                BlockPos pos = new BlockPos(x, y, z);
                boolean isXBlade = x>=bearingMin&&x<=bearingMax;
                boolean isYBlade = y>=bearingMin&&y<=bearingMax;
                if(isXBlade&&isYBlade)continue;//that's the bearing
                if(isXBlade||isYBlade)setBlock(pos, new Block(getConfiguration(), pos, block));
            }
        }
    }
    @Override
    public Multiblock<Block> newInstance(NCPFConfigurationContainer configuration, int x, int y, int z){
        return new OverhaulTurbine(configuration, x, z, null);
    }
    @Override
    public void getAvailableBlocks(List<Block> blocks){
        if(getSpecificConfiguration()==null)return;
        for(BlockElement block : getSpecificConfiguration().blocks){
            blocks.add(new Block(getConfiguration(), null, block));
        }
    }
    @Override
    public int getMinX(){
        return getSpecificConfiguration().settings.minWidth;
    }
    @Override
    public int getMinY(){
        return getSpecificConfiguration().settings.minWidth;
    }
    @Override
    public int getMinZ(){
        return getSpecificConfiguration().settings.minLength;
    }
    @Override
    public int getMaxX(){
        return getSpecificConfiguration().settings.maxSize;
    }
    @Override
    public int getMaxY(){
        return getSpecificConfiguration().settings.maxSize;
    }
    @Override
    public int getMaxZ(){
        return getSpecificConfiguration().settings.maxSize;
    }
    public void expandDiameter(int i){
        if(getInternalWidth()+i>getMaxX())return;
        blockGrids.clear();
        x += i;
        y += i;
        dimensions[0] += i;
        dimensions[1] += i;
        createBlockGrids();
        history.clear();
        future.clear();
    }
    public void contractDiameter(int i){
        if(getInternalWidth()-i<getMinX())return;
        blockGrids.clear();
        x -= i;
        y -= i;
        dimensions[0] -= i;
        dimensions[1] -= i;
        createBlockGrids();
        history.clear();
        future.clear();
    }
    @Override
    public void expand(int i, Direction direction){
        if(direction.x!=0||direction.y!=0){
            expandDiameter(i);
            return;
        }
        super.expand(i, direction);
    }
    @Override
    public void delete(int deletePos, Axis axis){
        if(axis.x!=0||axis.y!=0){
            contractDiameter(1);
            return;
        }
        super.delete(deletePos, axis);
    }
    @Override
    public void insert(int insertPos, Axis axis){
        if(axis.x!=0||axis.y!=0){
            expandDiameter(1);
            return;
        }
        super.delete(insertPos, axis);
    }
    @Override
    public void genCalcSubtasks(){
        calcCasing = calculateTask.addSubtask(new Task("Calculating Casing"));
        calcBearing = calculateTask.addSubtask(new Task("Calculating Bearing"));
        calcBlades = calculateTask.addSubtask(new Task("Calculating Blades"));
        calcRotor = calculateTask.addSubtask(new Task("Calculating Rotor"));
        calcCoils = calculateTask.addSubtask(new Task("Calculating Coils"));
        calcStats = calculateTask.addSubtask(new Task("Calculating Stats"));
    }
    @Override
    public boolean doCalculationStep(List<Block> blocks, boolean addDecals){
        if(calcSubstep>=10000)throw new RuntimeException("Calculation overflow on step "+calcStep+"! (Halted after "+calcSubstep+" iterations)");
        switch(calcStep){
            case 0://calculate casing
                numControllers = missingCasings = 0;
                hasInlet = hasOutlet = false;
                forEachCasingPosition((pos) -> {
                    Block block = getBlock(pos);
                    if(block==null){
                        missingCasings++;
                        if(addDecals)decals.enqueue(new MissingCasingDecal(pos));
                    }
                    if(block!=null){
                        if(block.template.inlet!=null)hasInlet = true;
                        if(block.template.outlet!=null)hasOutlet = true;
                        if(block.template.controller!=null)numControllers++;
                        if(block.template.casing!=null||block.template.inlet!=null||block.template.outlet!=null||block.template.controller!=null){
                            block.valid = true;
                            if(addDecals)decals.enqueue(new BlockValidDecal(pos));
                        }
                    }
                });
                calcCasing.finish();
                calcStep++;
                return true;
            case 1://calculate bearing
                int minBearingDiameter = getMinBearingDiameter();
                int maxBearingDiameter = getMaxBearingDiameter();
                Queue<Block> realToValidate = new Queue<>();
                BEARING:
                for(int i = minBearingDiameter; i<=maxBearingDiameter; i += 2){
                    Queue<Block> toValidate = new Queue<>();
                    int bearingMin = getExternalWidth()/2-i/2;
                    int bearingMax = getExternalWidth()/2+i/2-(i%2==0?1:0);
                    for(int x = bearingMin; x<=bearingMax; x++){
                        for(int y = bearingMin; y<=bearingMax; y++){
                            for(int z = 0; z<getExternalDepth(); z++){
                                Block block = getBlock(new BlockPos(x, y, z));
                                boolean valid = block!=null&&((z==0||z==getExternalDepth()-1)?block.template.bearing!=null:block.template.shaft!=null);
                                if(!valid)break BEARING;
                                toValidate.enqueue(block);
                            }
                        }
                    }
                    bearingDiameter = i;
                    realToValidate = toValidate;
                    calcBearing.progress = (i-minBearingDiameter)/(maxBearingDiameter-minBearingDiameter+1d);
                }
                for(Block b : realToValidate){
                    b.valid = true;
                    if(addDecals)decals.enqueue(new BlockValidDecal(b.pos));
                }
                calcBearing.finish();
                calcStep++;
                return true;
            case 2://calculate blades
                blades = new BlockElement[getInternalDepth()];
                bladesComplete = new boolean[getInternalDepth()];
                int bearingMin = getExternalWidth()/2-bearingDiameter/2;
                int bearingMax = getExternalWidth()/2+bearingDiameter/2-(bearingDiameter%2==0?1:0);
                for(int z = 1; z<=getInternalDepth(); z++){
                    boolean badBlade = false;
                    boolean bladeIncomplete = false;
                    Queue<Block> toValidate = new Queue<>();
                    for(int x = 1; x<=getInternalWidth(); x++){
                        for(int y = 1; y<=getInternalHeight(); y++){
                            BlockPos pos = new BlockPos(x, y, z);
                            Block block = getBlock(pos);
                            boolean xBlade = x>=bearingMin&&x<=bearingMax;
                            boolean yBlade = y>=bearingMin&&y<=bearingMax;
                            if(xBlade&&yBlade)continue;//that's a bearing, already done
                            if(!xBlade&&!yBlade){
                                if(block!=null&&addDecals)decals.enqueue(new BlockInvalidDecal(pos));
                                continue;
                            }
                            if(block==null){
                                decals.enqueue(new MissingBladeDecal(pos));
                                bladeIncomplete = true;
                            }else{
                                toValidate.enqueue(block);
                                if(blades[z-1]==null)blades[z-1] = block.template;
                                else if(blades[z-1]!=block.template)badBlade = true;
                            }
                        }
                    }
                    if(badBlade)blades[z-1] = null;
                    else{
                        for(Block b : toValidate){
                            b.valid = true;
                            if(addDecals)decals.enqueue(new BlockValidDecal(b.pos));
                        }
                    }
                    bladesComplete[z-1] = !bladeIncomplete;
                    calcBlades.progress = (z-1d)/getInternalDepth();
                }
                calcBlades.finish();
                calcStep++;
                return true;
            case 3://calculate rotor
                rotorValid = true;
                bladeCount = 0;
                for(BlockElement blade : blades){
                    if(blade==null)rotorValid = false;
                    else
                        bladeCount++;
                }
                if(rotorValid){
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
                        float expansion = blades[i].stator!=null?blades[i].stator.expansion:blades[i].blade.expansion;
                        if(blades[i].stator!=null){
                            minStatorExpansion = Math.min(expansion, minStatorExpansion);
                        }else{
                            numberOfBlades++;
                            numBlades += bearingDiameter*4*(getInternalWidth()/2-bearingDiameter/2);
                            minBladeExpansion = Math.min(expansion, minBladeExpansion);
                            maxBladeExpansion = Math.max(expansion, maxBladeExpansion);
                        }
                        idealExpansion[i] = MathUtil.pow(recipe.stats.coefficient, (i+.5f)/blades.length);
                        actualExpansion[i] = expansionSoFar*Math.sqrt(expansion);
                        expansionSoFar *= expansion;
                        rotorEfficiency += blades[i].blade==null?0:blades[i].blade.efficiency*Math.min(actualExpansion[i]/idealExpansion[i], idealExpansion[i]/actualExpansion[i]);
                    }
                    rotorEfficiency /= numberOfBlades;
                    maxInput = numBlades*getSpecificConfiguration().settings.fluidPerBlade;
                    maxUnsafeInput = maxInput*2;
                    int effectiveMaxLength;
                    if(minBladeExpansion<=1||minStatorExpansion>=1d){
                        effectiveMaxLength = getSpecificConfiguration().settings.maxSize;
                    }else{
                        effectiveMaxLength = (int)Math.ceil(Math.max(getSpecificConfiguration().settings.minLength, Math.min(getSpecificConfiguration().settings.maxSize, (MathUtil.log(recipe.stats.coefficient)-getSpecificConfiguration().settings.maxSize*MathUtil.log(minStatorExpansion))/(MathUtil.log(minBladeExpansion)-MathUtil.log(minStatorExpansion)))));
                    }
                    int bladeArea = bearingDiameter*4*(getInternalWidth()/2-bearingDiameter/2);
                    double rate = Math.min(getInputRate(), maxInput);
                    double lengthBonus = rate/(getSpecificConfiguration().settings.fluidPerBlade*bladeArea*effectiveMaxLength);
                    double areaBonus = Math.sqrt(2*rate/(getSpecificConfiguration().settings.fluidPerBlade*(getInternalDepth())*getSpecificConfiguration().settings.maxSize*effectiveMaxLength));
                    double effectiveMinLength = recipe.stats.coefficient<=1||maxBladeExpansion<=1?getSpecificConfiguration().settings.maxSize:Math.ceil(MathUtil.log(recipe.stats.coefficient)/MathUtil.log(maxBladeExpansion));
                    int minBladeArea = ((getSpecificConfiguration().settings.minWidth-1)*2);
                    double absoluteLeniency = effectiveMinLength*minBladeArea*getSpecificConfiguration().settings.fluidPerBlade;
                    double throughputRatio = maxInput==0?1:Math.min(1, (getInputRate()+absoluteLeniency)/maxInput);
                    double throughputEfficiencyMult = throughputRatio>=getSpecificConfiguration().settings.throughputEfficiencyLeniencyThreshold?1:(1-getSpecificConfiguration().settings.throughputEfficiencyLeniencyMultiplier)*Math.sin(throughputRatio*Math.PI/(2*getSpecificConfiguration().settings.throughputEfficiencyLeniencyThreshold))+getSpecificConfiguration().settings.throughputEfficiencyLeniencyMultiplier;
                    throughputEfficiency = (1+getSpecificConfiguration().settings.powerBonus*MathUtil.pow(lengthBonus*areaBonus, 2/3d))*throughputEfficiencyMult;
                    idealityMultiplier = Math.min(expansionSoFar, recipe.stats.coefficient)/Math.max(expansionSoFar, recipe.stats.coefficient);
                }
                calcRotor.finish();
                calcStep++;
                return true;
            case 4://calculate coils
                calcSubstep++;
                boolean somethingChanged = false;
                calcCoils.name = "Calculating Coils"+(calcSubstep>1?" ("+calcSubstep+")":"");
                for(int i = 0; i<blocks.size(); i++){
                    if(calculateCoil(blocks.get(i), addDecals))somethingChanged = true;
                    calcCoils.progress = i/(double)blocks.size();
                }
                if(somethingChanged)return true;
                calcCoils.finish();
                calcSubstep = 0;
                calcStep++;
                return true;
            case 5://calculate stats
                float inputEff = 0;
                float outputEff = 0;
                int inputCoils = 0;
                int outputCoils = 0;
                for(int x = 1; x<=getInternalWidth(); x++){
                    for(int y = 1; y<=getInternalHeight(); y++){
                        Block in = getBlock(new BlockPos(x, y, 0));
                        if(in!=null&&in.isCoil()&&in.isActive()){
                            inputEff += in.template.coil.efficiency;
                            inputCoils++;
                        }
                        Block out = getBlock(new BlockPos(x, y, getExternalDepth()-1));
                        if(out!=null&&out.isCoil()&&out.isActive()){
                            outputEff += out.template.coil.efficiency;
                            outputCoils++;
                        }
                    }
                }
                int bearings = bearingDiameter*bearingDiameter;
                inputEff /= Math.max(inputCoils, bearings/2);
                outputEff /= Math.max(outputCoils, bearings/2);
                if(Float.isNaN(inputEff))inputEff = 0;
                if(Float.isNaN(outputEff))outputEff = 0;
                coilEfficiency = (inputEff+outputEff)/2;
                totalEfficiency = coilEfficiency*rotorEfficiency*throughputEfficiency*idealityMultiplier;//*getConfiguration().overhaul.turbine.throughputEfficiencyLeniency;
                totalFluidEfficiency = totalEfficiency*recipe.stats.power;
                totalOutput = (long)(totalFluidEfficiency*getInputRate());
                safeOutput = (long)(totalFluidEfficiency*maxInput);
                unsafeOutput = (long)(totalFluidEfficiency*maxUnsafeInput);
                calcStats.finish();
                calcStep = 0;
                return false;
            default:
                throw new IllegalStateException("Invalid calculation step: "+calcStep+"!");
        }
    }
    /**
     * Calculates the coil
     *
     * @param reactor the reactor
     * @return <code>true</code> if the coil state has changed
     */
    public boolean calculateCoil(Block block, boolean addDecals){
        if(!block.isCoil()&&!block.isConnector())return false;
        boolean wasValid = block.valid;
        boolean hasAny = false;
        for(Direction d : Direction.values()){
            if(contains(block.pos.offset(d))){
                Block b = getBlock(block.pos.offset(d));
                if(b!=null&&(b.isCoil()||b.isConnector()||b.isBearing())&&b.isValid()){
                    hasAny = true;
                    break;
                }
            }
        }
        if(!hasAny){
            if(block.valid&&addDecals)decals.enqueue(new BlockInvalidDecal(block.pos));
            block.valid = false;
            return wasValid!=block.valid;
        }
        for(NCPFPlacementRule rule : block.getRules()){
            if(!rule.isValid(block, this)){
                if(block.valid&&addDecals)decals.enqueue(new BlockInvalidDecal(block.pos));
                block.valid = false;
                return wasValid!=block.valid;
            }
        }
        if(!block.valid&&addDecals)decals.enqueue(new BlockValidDecal(block.pos));
        block.valid = true;
        return wasValid!=block.valid;
    }
    @Override
    public FormattedText getTooltip(boolean full){
        String tooltip;
        FormattedText text = new FormattedText();
        if(numControllers<1)text.addText("No controller!", Core.theme.getTooltipInvalidTextColor());
        if(numControllers>1)text.addText("Too many controllers!", Core.theme.getTooltipInvalidTextColor());
        if(missingCasings>0)text.addText("Casing incomplete! (Missing "+missingCasings+")", Core.theme.getTooltipInvalidTextColor());
        if(!hasInlet)text.addText("Missing inlet!", Core.theme.getTooltipInvalidTextColor());
        if(!hasOutlet)text.addText("Missing outlet!", Core.theme.getTooltipInvalidTextColor());
        text.addText(bearingDiameter==0?"Invalid Bearing!":"Bearing Diameter: "+bearingDiameter, bearingDiameter==0?Core.theme.getTooltipInvalidTextColor():Core.theme.getTooltipTextColor());
        if(bladesComplete!=null){
            for(int i = 0; i<bladesComplete.length; i++){
                if(!bladesComplete[i])text.addText("Blade "+(i+1)+" incomplete!", Core.theme.getTooltipInvalidTextColor());
            }
        }
        if(rotorValid){
            tooltip = "Total output: "+totalOutput+" RF/t\n"
                +"Input: "+getInputRate()+"/"+maxInput+" mb/t\n"
                +"Power Efficiency: "+MathUtil.round(totalFluidEfficiency, 2)+" RF/mb\n"
                +"Total Efficiency: "+MathUtil.percent(totalEfficiency, 2)+"\n"
                +"Rotor Efficiency: "+MathUtil.percent(rotorEfficiency, 2)+"\n"
                +"Coil Efficiency: "+MathUtil.percent(coilEfficiency, 2)+"\n"
                +"Throughput Efficiency: "+MathUtil.percent(throughputEfficiency, 2)+"\n"
                +"Ideality Multiplier: "+MathUtil.percent(idealityMultiplier, 2);
        }else{
            tooltip = "Rotor Invalid!"+(blades==null?"":" ("+bladeCount+"/"+blades.length+")")+"\n"
                +"Input: "+getInputRate()+"/"+maxInput+" mb/t\n"
                +"Coil Efficiency: "+MathUtil.percent(coilEfficiency, 2);
        }
        tooltip += getModuleTooltip();
        text.addText(tooltip, rotorValid?Core.theme.getTooltipTextColor():Core.theme.getTooltipInvalidTextColor());
        return text;
    }
    @Override
    public boolean validate(){
        return false;
    }
    @Override
    public void clearData(List<Block> blocks){
        super.clearData(blocks);
        rotorValid = false;
        bladeCount = 0;
        bearingDiameter = 0;
        idealityMultiplier = throughputEfficiency = totalEfficiency = totalFluidEfficiency = rotorEfficiency = coilEfficiency = 0;
        totalOutput = safeOutput = unsafeOutput = maxInput = maxUnsafeInput = 0;
    }
    @Override
    public OverhaulTurbine blankCopy(){
        return new OverhaulTurbine(configuration, getInternalWidth(), getInternalDepth(), recipe);
    }
    @Override
    public OverhaulTurbine doCopy(){
        OverhaulTurbine copy = blankCopy();
        forEachPosition((pos) -> {
            copy.setBlock(pos, getBlock(pos));
        });
        copy.rotorValid = rotorValid;
        copy.bladeCount = bladeCount;
        copy.throughputEfficiency = throughputEfficiency;
        copy.idealityMultiplier = idealityMultiplier;
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
    public String getGeneralName(){
        return "Turbine";
    }
    @Override
    protected boolean isCompatible(Multiblock<Block> other){
        return ((OverhaulTurbine)other).recipe==recipe;
    }
    private int getInputRate(){
        return maxInput;
//        if(inputs.isEmpty())return maxInput;
//        int input = 0;
//        for(Multiblock m : inputs){
//            ArrayList<FluidStack> outs = m.getFluidOutputs();
//            for(FluidStack stack : outs){
//                if(stack.name.equals(recipe.inputName))input+=stack.amount;
//            }
//        }
//        return input;
    }
    @Override
    protected void getMainParts(ArrayList<PartCount> parts){
        HashMap<Block, Integer> blocks = new HashMap<>();
        FOR:
        for(Block block : getBlocks(true)){
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
    }
    @Override
    protected float[] getCubeBounds(Block block){
        int bearingMax = getExternalWidth()/2+bearingDiameter/2;
        int bearingMin = getExternalWidth()/2-bearingDiameter/2;
        boolean isXBlade = block.pos.x>=bearingMin&&block.pos.x<=bearingMax;
        boolean isYBlade = block.pos.y>=bearingMin&&block.pos.y<=bearingMax;
        if(block.isBlade()){
            float x1 = 0;
            float y1 = 0;
            float z1 = 0;
            float x2 = 1;
            float y2 = 1;
            float z2 = 1;
            if(block.template.stator!=null){
                if(isXBlade){//side
                    x1 += 7/16f;
                    x2 -= 7/16f;
                    z1 += 2/16f;
                    z2 -= 2/16f;
                }
                if(isYBlade){//top
                    y1 += 7/16f;
                    y2 -= 7/16f;
                    z1 += 2/16f;
                    z2 -= 2/16f;
                }
            }else{//blade
                if(isXBlade){//side
                    z1 += 7/16f;
                    z2 -= 7/16f;
                    x1 += 2/16f;
                    x2 -= 2/16f;
                }
                if(isYBlade){//top
                    z1 += 7/16f;
                    z2 -= 7/16f;
                    y1 += 2/16f;
                    y2 -= 2/16f;
                }
            }
            return new float[]{x1, y1, z1, x2, y2, z2};
        }
        return super.getCubeBounds(block);
    }
    @Override
    public String getDescriptionTooltip(){
        return "Overhaul Turbines are Turbines in NuclearCraft: Overhauled";
    }
    @Override
    public void getSuggestors(ArrayList<Suggestor> suggestors){
        suggestors.add(new Suggestor<OverhaulTurbine>("Coil Suggestor", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<OverhaulTurbine>("Efficiency", true, true){
                    @Override
                    protected double doCompare(OverhaulTurbine main, OverhaulTurbine other){
                        return main.coilEfficiency-other.coilEfficiency;
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Suggests adding, removing, or replacing coils for higher coil efficiency";
            }
            @Override
            public void generateSuggestions(OverhaulTurbine multiblock, Suggestor.SuggestionAcceptor suggestor){
                ArrayList<Block> blocks = new ArrayList<>();
                multiblock.getAvailableBlocks(blocks);
                for(Iterator<Block> it = blocks.iterator(); it.hasNext();){
                    Block b = it.next();
                    if(!b.isCoil())it.remove();
                }
                suggestor.setCount(multiblock.getInternalWidth()*multiblock.getInternalHeight()*2*(blocks.size()+1));
                for(int x = 1; x<=multiblock.getInternalWidth(); x++){
                    for(int y = 1; y<=multiblock.getInternalHeight(); y++){
                        for(int z = 0; z<2; z++){
                            if(z==1)z = multiblock.getExternalDepth()-1;
                            BlockPos pos = new BlockPos(x,y,z);
                            Block block = multiblock.getBlock(pos);
                            if(block!=null&&block.isBearing()){
                                suggestor.task.max--;
                                continue;
                            }
                            for(Block newBlock : blocks){
                                if(newBlock.template.coil.efficiency>(block==null||!block.isCoil()?0:block.template.coil.efficiency)&&multiblock.isValid(newBlock, pos))suggestor.suggest(new Suggestion(block==null?"Add "+newBlock.getName():"Replace "+block.getName()+" with "+newBlock.getName(), new SetblockAction(pos, newBlock), priorities));
                                else
                                    suggestor.task.max--;
                            }
                            if(block!=null){
                                suggestor.task.max++;
                                suggestor.suggest(new Suggestion("Remove "+block.getName(), new SetblockAction(pos, null), priorities));
                            }
                        }
                    }
                }
            }
        });
        suggestors.add(new Suggestor<OverhaulTurbine>("Blade Suggestor", -1, -1){
            ArrayList<Priority> priorities = new ArrayList<>();
            {
                priorities.add(new Priority<OverhaulTurbine>("Efficiency", true, true){
                    @Override
                    protected double doCompare(OverhaulTurbine main, OverhaulTurbine other){
                        return (main.rotorEfficiency*main.idealityMultiplier)-(other.rotorEfficiency*other.idealityMultiplier);
                    }
                });
            }
            @Override
            public String getDescription(){
                return "Suggests blades and stators for higher rotor efficiency and ideality";
            }
            @Override
            public void generateSuggestions(OverhaulTurbine multiblock, Suggestor.SuggestionAcceptor suggestor){
                ArrayList<Block> blades = new ArrayList<>();
                multiblock.getAvailableBlocks(blades);
                for(Iterator<Block> it = blades.iterator(); it.hasNext();){
                    Block b = it.next();
                    if(!b.isBlade())it.remove();
                }
                int bladeSize = (getInternalWidth()-bearingDiameter)/2;
                for(int z = 1; z<getExternalDepth()-1; z++){
                    BlockPos pos = new BlockPos(x,y,z);
                    Block block = multiblock.getBlock(pos);
                    for(Block newBlock : blades){
                        SetblocksAction action = new SetblocksAction(newBlock);
                        for(int x = 1; x<multiblock.getExternalWidth()-1; x++){
                            for(int y = 1; y<multiblock.getExternalHeight()-1; y++){
                                int numEdges = 0;
                                if(x<=bladeSize)numEdges++;
                                if(x>bladeSize+bearingDiameter)numEdges++;
                                if(y<=bladeSize)numEdges++;
                                if(y>bladeSize+bearingDiameter)numEdges++;
                                if(numEdges==1)action.add(pos);
                            }
                        }
                        suggestor.suggest(new Suggestion(block==null?"Add "+newBlock.getName():"Replace "+block.getName()+" with "+newBlock.getName(), action, priorities));
                    }
                }
            }
        });
    }
    @Override
    public void getEditorSpaces(ArrayList<EditorSpace<Block>> editorSpaces){
        editorSpaces.add(new EditorSpace<Block>(0, 0, 0, x+1, y+1, z+1){
            @Override
            public boolean isSpaceValid(Block block, BlockPos pos){
                if(block==null)return true;
                boolean x0 = pos.x==0;
                boolean y0 = pos.y==0;
                boolean z0 = pos.z==0;
                boolean x1 = pos.x==OverhaulTurbine.this.x+1;
                boolean y1 = pos.y==OverhaulTurbine.this.y+1;
                boolean z1 = pos.z==OverhaulTurbine.this.z+1;
                int bearingMaxMin = 2;
                int bearingMaxMax = getInternalWidth()-1;
                boolean even = getInternalWidth()%2==0;
                int bearingMinMin = getExternalWidth()/2-(even?1:0);
                int bearingMinMax = getExternalWidth()/2;
                boolean canBeXBlade = pos.x>=bearingMaxMin&&pos.x<=bearingMaxMax;
                boolean canBeYBlade = pos.y>=bearingMaxMin&&pos.y<=bearingMaxMax;
                boolean mustBeXBlade = pos.x>=bearingMinMin&&pos.x<=bearingMinMax;
                boolean mustBeYBlade = pos.y>=bearingMinMin&&pos.y<=bearingMinMax;
                if(x0||y0||z0||x1||y1||z1){
                    if(x0&&y0||x0&&z0||x0&&x1||x0&&y1||x0&&z1||y0&&z0||y0&&x1||y0&&y1||y0&&z1||z0&&x1||z0&&y1||z0&&z1||x1&&y1||x1&&z1||y1&&z1){
                        return block.template.casing!=null&&block.template.casing.edge;
                    }else{
                        if(x0||y0||x1||y1)return block.template.casing!=null;//a side
                        if(mustBeXBlade&&mustBeYBlade)return block.template.bearing!=null;
                        if(canBeXBlade&&canBeYBlade)return block.template.bearing!=null||block.template.casing!=null||block.template.coil!=null||block.template.connector!=null||(z0?block.template.inlet!=null:block.template.outlet!=null);
                        return block.template.casing!=null||block.template.coil!=null||block.template.connector!=null||(z0?block.template.inlet!=null:block.template.outlet!=null);
                    }
                }
                if(mustBeXBlade&&mustBeYBlade)return block.template.shaft!=null;
                if(canBeXBlade&&canBeYBlade)return block.template.shaft!=null||block.template.blade!=null||block.template.stator!=null;
                if(!canBeXBlade&&!canBeYBlade)return false;
                return block.template.blade!=null||block.template.stator!=null;
            }
            @Override
            public void createComponents(MenuEdit editor, ArrayList<Component> comps, int cellSize){
                comps.add(new MenuComponentEditorGrid(0, 0, cellSize, editor, OverhaulTurbine.this, this, 1, 1, OverhaulTurbine.this.x, OverhaulTurbine.this.y, Axis.Z, 0));
                comps.add(new MenuComponentEditorGrid(0, 0, cellSize, editor, OverhaulTurbine.this, this, 1, 1, OverhaulTurbine.this.x, OverhaulTurbine.this.y, Axis.Z, OverhaulTurbine.this.z+1));
                for(int y = 0; y<=OverhaulTurbine.this.y+1; y++){
                    comps.add(new MenuComponentEditorGrid(0, 0, cellSize, editor, OverhaulTurbine.this, this, 0, 0, OverhaulTurbine.this.z+1, OverhaulTurbine.this.x+1, Axis.Y_INVERTED, y));
                }
            }
        });
    }
    @Override
    public boolean canBePlacedInCasingEdge(Block b){
        return true;
    }
    @Override
    public boolean canBePlacedInCasingFace(Block b){
        return true;
    }
    @Override
    public boolean canBePlacedWithinCasing(Block b){
        return true;
    }
    @Override
    public void buildDefaultCasing(){
        Block casing = null;
        Block window = null;
        Block controller = null;
        Block inlet = null;
        Block outlet = null;
        for(BlockElement template : getSpecificConfiguration().blocks){
            if(template.casing!=null&&template.casing.edge)casing = new Block(getConfiguration(), null, template);
            if(template.casing!=null&&!template.casing.edge&&template.controller==null)window = new Block(getConfiguration(), null, template);
            if(template.controller!=null)controller = new Block(getConfiguration(), null, template);
            if(template.inlet!=null)inlet = new Block(getConfiguration(), null, template);
            if(template.outlet!=null)outlet = new Block(getConfiguration(), null, template);
        }
        final Block theCasing = casing;
        final Block theWindow = window==null?casing:window;
        final Block theController = controller;
        final Block theInlet = inlet;
        final Block theOutlet = outlet;
        boolean[] hasPlacedTheController = new boolean[1];
        boolean[] hasPlacedTheInlet = new boolean[1];
        boolean[] hasPlacedTheOutlet = new boolean[1];
        for(Block block : getBlocks()){
            if(block.template.controller!=null)hasPlacedTheController[0] = true;
            if(block.template.inlet!=null)hasPlacedTheInlet[0] = true;
            if(block.template.outlet!=null)hasPlacedTheOutlet[0] = true;
        }
        forEachCasingFacePosition((pos) -> {
            if(getBlock(pos)!=null){
                if(getBlock(pos).template!=theCasing.template&&getBlock(pos).template!=theWindow.template)return;
            }
            if(!hasPlacedTheController[0]){
                setBlock(pos, theController);
                hasPlacedTheController[0] = true;
                return;
            }
            if(z==0&&!hasPlacedTheInlet[0]){
                setBlock(pos, theInlet);
                hasPlacedTheInlet[0] = true;
                return;
            }
            if(z==getExternalDepth()-1&&!hasPlacedTheOutlet[0]){
                setBlock(pos, theOutlet);
                hasPlacedTheOutlet[0] = true;
                return;
            }
        });
        forEachCasingEdgePosition((pos) -> {
            if(getBlock(pos)!=null)return;
            setBlock(pos, theCasing);
        });
        forEachCasingFacePosition((pos) -> {
            if(getBlock(pos)!=null)return;
            setBlock(pos, theWindow);
        });
    }
    public int getMinBearingDiameter(){
        return getInternalWidth()%2==0?2:1;
    }
    public int getMaxBearingDiameter(){
        return getInternalWidth()-2;
    }
    @Override
    public String getPreviewTexture(){
        return "multiblocks/overhaul_turbine";
    }
    @Override
    public void init(){
        super.init();
        setBearing(1);
    }
    @Override
    public LiteMultiblock<OverhaulTurbine> compile(){
        return null;
    }
    @Override
    public boolean shouldHideWithCasing(BlockPos pos){
        return pos.x==0||pos.y==0||pos.x==getExternalWidth()-1||pos.y==getExternalHeight()-1;
    }
    @Override
    public BoundingBox getBoundingBox(boolean includeCasing){
        BoundingBox bbox = super.getBoundingBox(includeCasing);
        if(!includeCasing){
            bbox = new BoundingBox(bbox.x1, bbox.y1, bbox.z1-1, bbox.x2, bbox.y2, bbox.z2+1);
        }
        return bbox;
    }
    @Override
    public OverhaulTurbineDesign convertToDesign(){
        OverhaulTurbineDesign design = new OverhaulTurbineDesign(Core.project, x, y, z);
        forEachPosition((pos) -> {
            Block block = getBlock(pos);
            design.design[x][y][z] = block==null?null:block.template;
        });
        design.recipe = recipe;
        return design;
    }
    @Override
    public NCPFElement[] getMultiblockRecipes(){
        return new NCPFElement[]{recipe};
    }
    @Override
    public void setMultiblockRecipe(int recipeType, NCPFElement recipe){
        this.recipe = (TurbineRecipe)recipe;
    }
}

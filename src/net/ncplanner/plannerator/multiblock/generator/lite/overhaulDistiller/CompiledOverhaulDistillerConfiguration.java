package net.ncplanner.plannerator.multiblock.generator.lite.overhaulDistiller;
import com.thizthizzydizzy.dizzyengine.graphics.image.Image;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;
import net.ncplanner.plannerator.multiblock.generator.lite.CompiledConfiguration;
import net.ncplanner.plannerator.ncpf.NCPFElementStack;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyFluidElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyRecipeElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulDistillerConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulDistiller.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulDistiller.DistillerRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.configuration.settings.OverhaulDistillerSettingsModule;
public class CompiledOverhaulDistillerConfiguration implements CompiledConfiguration{
    public int minSize, maxSize, baseTime, basePower;
    public NCPFElementDefinition[] blockDefinition, recipeDefinition;
    public String[] blockDisplayName, recipeDisplayName;
    public Image[] blockTexture, blockDisplayTexture, recipeDisplayTexture;
    public boolean[] blockCasing, blockEdge, blockController, blockInputPort, blockOutputPort;
    public boolean[] blockTray, blockSieve, blockReflux, blockReboiling, blockDistributor, blockPlainCasing;
    public double[] blockEfficiency, recipeTime, recipePower, recipeGasFraction, recipeLiquidFraction;
    public boolean[] recipeHasStats;
    public int[] recipeTrays, recipeInputPorts, recipeOutputPorts;
    public static boolean matchesRecipe(NCPFElementDefinition a, NCPFElementDefinition b){
        if(a==b)return true;
        // Legacy recipe definitions contain stacks, which the generic settings matcher does not compare.
        if(a instanceof NCPFLegacyRecipeElement&&b instanceof NCPFLegacyRecipeElement){
            NCPFLegacyRecipeElement first = (NCPFLegacyRecipeElement)a, second = (NCPFLegacyRecipeElement)b;
            return matchesStacks(first.inputs, second.inputs)&&matchesStacks(first.outputs, second.outputs);
        }
        return a!=null&&b!=null&&a.matches(b);
    }
    private static boolean matchesStacks(Set<NCPFElementStack> a, Set<NCPFElementStack> b){
        if(a.size()!=b.size())return false;
        ArrayList<NCPFElementStack> remaining = new ArrayList<>(b);
        for(NCPFElementStack stack : a){
            int match = -1;
            for(int i = 0; i<remaining.size(); i++){
                NCPFElementStack other = remaining.get(i);
                if(stack.amount==other.amount&&stack.definition.matches(other.definition)){match = i;break;}
            }
            if(match<0)return false;
            remaining.remove(match);
        }
        return true;
    }
    public static CompiledOverhaulDistillerConfiguration compile(OverhaulDistillerConfiguration config){
        CompiledOverhaulDistillerConfiguration c = new CompiledOverhaulDistillerConfiguration();
        OverhaulDistillerSettingsModule s = config.settings==null?new OverhaulDistillerSettingsModule():config.settings;
        c.minSize = s.minSize;
        c.maxSize = s.maxSize;
        c.baseTime = s.baseTime;
        c.basePower = s.basePower;
        int n = config.blocks.size();
        c.blockDefinition = new NCPFElementDefinition[n];
        c.blockDisplayName = new String[n];
        c.blockTexture = new Image[n];
        c.blockDisplayTexture = new Image[n];
        c.blockCasing = new boolean[n];
        c.blockEdge = new boolean[n];
        c.blockController = new boolean[n];
        c.blockInputPort = new boolean[n];
        c.blockOutputPort = new boolean[n];
        c.blockTray = new boolean[n];
        c.blockSieve = new boolean[n];
        c.blockReflux = new boolean[n];
        c.blockReboiling = new boolean[n];
        c.blockDistributor = new boolean[n];
        c.blockPlainCasing = new boolean[n];
        c.blockEfficiency = new double[n];
        for(int i = 0; i<n; i++){
            BlockElement b = config.blocks.get(i);
            c.blockDefinition[i] = b.definition;
            c.blockDisplayName[i] = b.getDisplayName();
            c.blockTexture[i] = b.getTexture();
            c.blockDisplayTexture[i] = b.getDisplayTexture();
            c.blockEdge[i] = b.casing!=null&&b.casing.edge;
            c.blockController[i] = b.controller!=null;
            c.blockInputPort[i] = b.processPort!=null&&!b.processPort.output;
            c.blockOutputPort[i] = b.processPort!=null&&b.processPort.output;
            c.blockTray[i] = b.sieveTray!=null;
            c.blockSieve[i] = b.sieveAssembly!=null;
            c.blockReflux[i] = b.refluxUnit!=null;
            c.blockReboiling[i] = b.reboilingUnit!=null;
            c.blockDistributor[i] = b.liquidDistributor!=null;
            c.blockCasing[i] = b.casing!=null||b.controller!=null||b.port!=null||b.powerPort!=null
                    ||b.processPort!=null||b.reservoirPort!=null||b.refluxUnit!=null||b.reboilingUnit!=null||b.liquidDistributor!=null;
            c.blockPlainCasing[i] = b.casing!=null&&b.controller==null&&b.port==null&&b.powerPort==null
                    &&b.processPort==null&&b.reservoirPort==null&&b.refluxUnit==null&&b.reboilingUnit==null&&b.liquidDistributor==null;
            c.blockEfficiency[i] = b.sieveAssembly==null?0:b.sieveAssembly.efficiency;
        }
        n = config.recipes.size();
        c.recipeDefinition = new NCPFElementDefinition[n];
        c.recipeDisplayName = new String[n];
        c.recipeDisplayTexture = new Image[n];
        c.recipeTime = new double[n];
        c.recipePower = new double[n];
        c.recipeGasFraction = new double[n];
        c.recipeLiquidFraction = new double[n];
        c.recipeHasStats = new boolean[n];
        c.recipeTrays = new int[n];
        c.recipeInputPorts = new int[n];
        c.recipeOutputPorts = new int[n];
        for(int i = 0; i<n; i++){
            DistillerRecipe r = config.recipes.get(i);
            c.recipeDefinition[i] = r.definition;
            c.recipeDisplayName[i] = r.getDisplayName();
            c.recipeDisplayTexture[i] = r.getDisplayTexture();
            c.recipeHasStats[i] = r.stats!=null;
            if(r.stats!=null){
                c.recipeTime[i] = r.stats.timeMult;
                c.recipePower[i] = r.stats.powerMult;
                c.recipeTrays[i] = r.stats.sieveTrayCount;
            }
            int gas = 0, liquid = 0;
            for(Set<NCPFElementStack> stacks : Arrays.asList(r.getRecipeDefinition().inputs, r.getRecipeDefinition().outputs)){
                for(NCPFElementStack stack : stacks){
                    if(stack.amount<=0||!(stack.definition instanceof NCPFLegacyFluidElement))continue;
                    NCPFLegacyFluidElement f = (NCPFLegacyFluidElement)stack.definition;
                    if(f.name==null||f.name.isEmpty())continue;
                    if(f.gaseous)gas++;
                    else liquid++;
                }
            }
            if(gas+liquid>0){
                c.recipeGasFraction[i] = (double)gas/(gas+liquid);
                c.recipeLiquidFraction[i] = (double)liquid/(gas+liquid);
            }
            for(NCPFElementStack stack : r.getRecipeDefinition().inputs)if(stack.amount>0)c.recipeInputPorts[i]++;
            for(NCPFElementStack stack : r.getRecipeDefinition().outputs)if(stack.amount>0)c.recipeOutputPorts[i]++;
        }
        return c;
    }
}

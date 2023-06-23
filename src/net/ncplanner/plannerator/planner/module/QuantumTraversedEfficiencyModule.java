package net.ncplanner.plannerator.planner.module;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.planner.MathUtil;
public class QuantumTraversedEfficiencyModule extends Module<Float>{
    public QuantumTraversedEfficiencyModule(){
        super("quantum_traversed_efficiency_score");
    }
    @Override
    public String getDisplayName(){
        return "Quantum Traversed Efficiency Score";
    }
    @Override
    public String getDescription(){
        return "Gives an adjusted efficiency score for overhaul SFRs based on neutron shield output";
    }
    @Override
    public Float calculateMultiblock(Multiblock m){
        if(m instanceof OverhaulSFR){
            OverhaulSFR sfr = (OverhaulSFR) m;
            float effeat = 0;
            for(Block b : sfr.getBlocks(true)){
                if(b.isFuelCellActive()){
                    effeat+=b.recipe.fuelCellEfficiency*b.recipe.fuelCellHeat;
                }
            }
            return (sfr.rawOutput*sfr.sparsityMult)/effeat;
        }
        return null;
    }
    @Override
    public String getTooltip(Multiblock m, Float o){
        return "Quantum Traversed Efficiency: "+MathUtil.percent(o, 0);
    }
}
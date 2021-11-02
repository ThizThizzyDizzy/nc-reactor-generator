package net.ncplanner.plannerator.planner.module;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
public class PrimeFuelModule extends Module<Integer>{
    public PrimeFuelModule(){
        super("prime_fuel");
    }
    @Override
    public String getDisplayName(){
        return "Prime Fuels";
    }
    @Override
    public String getDescription(){
        return "Highlights the number of cells/vessels in a multiblock, and shows if it's a prime number";
    }
    @Override
    public Integer calculateMultiblock(Multiblock m){
        if(m instanceof UnderhaulSFR){
            int totalCells = 0;
            ArrayList<net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block> blocks = m.getBlocks();
            for(net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block b : blocks){
                if(b.template.fuelCell)totalCells++;
            }
            return totalCells;
        }
        if(m instanceof OverhaulSFR){
            int totalCells = 0;
            ArrayList<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block> blocks = m.getBlocks();
            for(net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block b : blocks){
                if(b.template.fuelCell)totalCells++;
            }
            return totalCells;
        }
        if(m instanceof OverhaulMSR){
            int totalVessels = 0;
            ArrayList<net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block> blocks = m.getBlocks();
            for(net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block b : blocks){
                if(b.template.fuelVessel)totalVessels++;
            }
            return totalVessels;
        }
        return null;
    }
    @Override
    public String getTooltip(Multiblock m, Integer o){
        if(m instanceof UnderhaulSFR||m instanceof OverhaulSFR||m instanceof OverhaulMSR){
            return (o>2?"Previous Prime: "+nextPrime(o, -1)+"\n":"")+"Fuels: "+o+(isPrime(o)?" (Prime)":"")+"\nNext prime: "+nextPrime(o, 1);
        }
        return "Rainbow Score: "+percent(o, 2);
    }
    private boolean isPrime(int n){
        if(n<=1)return false;for(int i = 2; i<n; i++){
            if(n%i==0)return false;
        }
        return true;
    }
    private int nextPrime(int n, int step){
        if(n<=1)return 2;
        if(isPrime(n+step))return n+step;
        return nextPrime(n+step, step);
    }
}
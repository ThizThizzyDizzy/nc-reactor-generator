package net.ncplanner.plannerator.multiblock.editor.symmetry;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.Block;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.planner.StringUtil;
public abstract class CoilSymmetry extends Symmetry<OverhaulTurbine>{
    public static CoilSymmetry X = new CoilSymmetry("X"){
        @Override
        public void apply(OverhaulTurbine multiblock){
            ArrayList<Block> bls = multiblock.getBlocks(true);
            for(Block b : bls){
                if(b.x==0||b.y==0||b.x==multiblock.getExternalWidth()-1||b.y==multiblock.getExternalHeight()-1)continue;
                if(b.template.blade)continue;
                int X = multiblock.getExternalWidth()-b.x-1;
                multiblock.setBlock(X, b.y, b.z, multiblock.getBlock(b.x, b.y, b.z));
            }
        }
    };
    public static CoilSymmetry Y = new CoilSymmetry("Y"){
        @Override
        public void apply(OverhaulTurbine multiblock){
            ArrayList<Block> bls = multiblock.getBlocks(true);
            for(Block b : bls){
                if(b.x==0||b.y==0||b.x==multiblock.getExternalWidth()-1||b.y==multiblock.getExternalHeight()-1)continue;
                if(b.template.blade)continue;
                int Y = multiblock.getExternalHeight()-b.y-1;
                multiblock.setBlock(b.x, Y, b.z, multiblock.getBlock(b.x, b.y, b.z));
            }
        }
    };
    public static CoilSymmetry Z = new CoilSymmetry("Z"){
        @Override
        public void apply(OverhaulTurbine multiblock){
            ArrayList<Block> bls = multiblock.getBlocks(true);
            for(Block b : bls){
                if(b.x==0||b.y==0||b.x==multiblock.getExternalWidth()-1||b.y==multiblock.getExternalHeight()-1)continue;
                if(b.template.blade)continue;
                int Z = multiblock.getExternalDepth()-b.z-1;
                multiblock.setBlock(b.x, b.y, Z, multiblock.getBlock(b.x, b.y, b.z));
            }
        }
    };
    public CoilSymmetry(String axis){
        super(axis+" Coil Symmetry");
    }
    public boolean matches(String sym){
        switch(StringUtil.superRemove(StringUtil.toLowerCase(sym), " ", "-", "symmetry", "symmetrical")){
            case "x":
                return this==X;
            case "y":
                return this==Y;
            case "z":
                return this==Z;
            case "xy":
                return this==X||this==Y;
            case "yz":
                return this==Y||this==Z;
            case "xz":
                return this==X||this==Z;
            case "xyz":
                return this==X||this==Y||this==Z;
            case "":
                return true;
        }
        return false;
    }
}
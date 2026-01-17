package net.ncplanner.plannerator.multiblock.editor.symmetry;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.CuboidalMultiblock;
import net.ncplanner.plannerator.planner.StringUtil;
public abstract class AxialSymmetry extends Symmetry<CuboidalMultiblock>{
    public static AxialSymmetry X = new AxialSymmetry("X"){
        @Override
        public void apply(CuboidalMultiblock multiblock){
            ArrayList<AbstractBlock> bls = multiblock.getBlocks(true);
            for(AbstractBlock b : bls){
                if(b.pos.x==0||b.pos.y==0||b.pos.z==0||b.pos.x==multiblock.getExternalWidth()-1||b.pos.y==multiblock.getExternalHeight()-1||b.pos.z==multiblock.getExternalDepth()-1)continue;
                int X = multiblock.getExternalWidth()-b.pos.x-1;
                multiblock.setBlock(new BlockPos(X, b.pos.y, b.pos.z), multiblock.getBlock(b.pos));
            }
        }
    };
    public static AxialSymmetry Y = new AxialSymmetry("Y"){
        @Override
        public void apply(CuboidalMultiblock multiblock){
            ArrayList<AbstractBlock> bls = multiblock.getBlocks(true);
            for(AbstractBlock b : bls){
                if(b.pos.x==0||b.pos.y==0||b.pos.z==0||b.pos.x==multiblock.getExternalWidth()-1||b.pos.y==multiblock.getExternalHeight()-1||b.pos.z==multiblock.getExternalDepth()-1)continue;
                int Y = multiblock.getExternalHeight()-b.pos.y-1;
                multiblock.setBlock(new BlockPos(b.pos.x, Y, b.pos.z), multiblock.getBlock(b.pos));
            }
        }
    };
    public static AxialSymmetry Z = new AxialSymmetry("Z"){
        @Override
        public void apply(CuboidalMultiblock multiblock){
            ArrayList<AbstractBlock> bls = multiblock.getBlocks(true);
            for(AbstractBlock b : bls){
                if(b.pos.x==0||b.pos.y==0||b.pos.z==0||b.pos.x==multiblock.getExternalWidth()-1||b.pos.y==multiblock.getExternalHeight()-1||b.pos.z==multiblock.getExternalDepth()-1)continue;
                int Z = multiblock.getExternalDepth()-b.pos.z-1;
                multiblock.setBlock(new BlockPos(b.pos.x, b.pos.y, Z), multiblock.getBlock(b.pos));
            }
        }
    };
    public AxialSymmetry(String axis){
        super(axis+" Symmetry");
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
package multiblock.symmetry;
import java.util.ArrayList;
import java.util.Locale;
import multiblock.Block;
import multiblock.Multiblock;
import multiblock.overhaul.turbine.OverhaulTurbine;
public abstract class CoilSymmetry extends Symmetry{
    public static CoilSymmetry X = new CoilSymmetry("X"){
        @Override
        public void apply(Multiblock multiblock){
            if(multiblock instanceof OverhaulTurbine){
                ArrayList<Block> bls = multiblock.getBlocks(true);
                for(Block b : bls){
                    if(((multiblock.overhaul.turbine.Block)b).blade!=null)continue;
                    int X = multiblock.getX()-b.x-1;
                    multiblock.setBlock(X, b.y, b.z, multiblock.getBlock(b.x, b.y, b.z));
                }
            }else{
                throw new IllegalArgumentException("Coil symmetry can only be applied to turbines!");
            }
        }
    };
    public static CoilSymmetry Y = new CoilSymmetry("Y"){
        @Override
        public void apply(Multiblock multiblock){
            if(multiblock instanceof OverhaulTurbine){
                ArrayList<Block> bls = multiblock.getBlocks(true);
                for(Block b : bls){
                    if(((multiblock.overhaul.turbine.Block)b).blade!=null)continue;
                    int Y = multiblock.getY()-b.y-1;
                    multiblock.setBlock(b.x, Y, b.z, multiblock.getBlock(b.x, b.y, b.z));
                }
            }else{
                throw new IllegalArgumentException("Coil symmetry can only be applied to turbines!");
            }
        }
    };
    public static CoilSymmetry Z = new CoilSymmetry("Z"){
        @Override
        public void apply(Multiblock multiblock){
            if(multiblock instanceof OverhaulTurbine){
                ArrayList<Block> bls = multiblock.getBlocks(true);
                for(Block b : bls){
                    if(((multiblock.overhaul.turbine.Block)b).blade!=null)continue;
                    int Z = multiblock.getZ()-b.z-1;
                    multiblock.setBlock(b.x, b.y, Z, multiblock.getBlock(b.x, b.y, b.z));
                }
            }else{
                throw new IllegalArgumentException("Coil symmetry can only be applied to turbines!");
            }
        }
    };
    public CoilSymmetry(String axis){
        super(axis+" Coil Symmetry");
    }
    public boolean matches(String sym){
        switch(sym.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("-", "").replace("symmetry", "").replace("symmetrical", "")){
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
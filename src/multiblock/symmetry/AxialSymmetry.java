package multiblock.symmetry;
public class AxialSymmetry extends Symmetry{
    public static AxialSymmetry X = new AxialSymmetry("X");
    public static AxialSymmetry Y = new AxialSymmetry("Y");
    public static AxialSymmetry Z = new AxialSymmetry("Z");
    public AxialSymmetry(String axis){
        super(axis+" Symmetry");
    }
}
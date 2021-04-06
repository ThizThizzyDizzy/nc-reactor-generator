package multiblock.decal;
import multiblock.Axis;
import multiblock.Decal;
import planner.Core;
public class UnderhaulModeratorLineDecal extends Decal{
    private final Axis axis;
    public UnderhaulModeratorLineDecal(int x, int y, int z, Axis axis){
        super(x, y, z);
        this.axis = axis;
    }
    @Override
    public void render(double x, double y, double blockSize){
        Core.applyColor(Core.theme.getBlue());
        switch(axis){
            case X:
                drawRect(x, y+blockSize*.375, x+blockSize, y+blockSize*.625, 0);
                break;
            case Y:
                drawRect(x+blockSize*.375, y+blockSize*.375, x+blockSize*.625, y+blockSize*.625, 0);
                break;
            case Z:
                drawRect(x+blockSize*.375, y, x+blockSize*.625, y+blockSize, 0);
                break;
        }
    }
    @Override
    public void render3D(double x, double y, double z, double blockSize){
        //TODO VR DECAL: adjacent moderator
    }
    @Override
    public String getTooltip(){
        return "Valid moderator line ("+axis.toString()+")";
    }
}
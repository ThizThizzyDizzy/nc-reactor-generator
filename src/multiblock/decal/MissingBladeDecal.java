package multiblock.decal;
import multiblock.Decal;
import planner.Core;
import planner.vr.VRCore;
public class MissingBladeDecal extends Decal{
    public MissingBladeDecal(int x, int y, int z){
        super(x, y, z);
    }
    @Override
    public void render(double x, double y, double blockSize){
        Core.applyColor(Core.theme.getRed());
        drawRect(x+blockSize*.375, y+blockSize*.375, x+blockSize*.625, y+blockSize*.625, 0);
    }
    @Override
    public void render3D(double x, double y, double z, double blockSize){
        Core.applyColor(Core.theme.getRed());
        VRCore.drawCube(x+blockSize*.375, y+blockSize*.375, z+blockSize*.375, x+blockSize*.625, y+blockSize*.625, z+blockSize*.625, 0);
    }
    @Override
    public String getTooltip(){
        return "Missing blade";
    }
}
package multiblock.decal;
import multiblock.Decal;
import planner.Core;
import planner.vr.VRCore;
public class BlockInvalidDecal extends Decal{
    public BlockInvalidDecal(int x, int y, int z){
        super(x, y, z);
    }
    @Override
    public void render(double x, double y, double blockSize){
        Core.applyColor(Core.theme.getRed(), .125f);
        drawRect(x, y, x+blockSize, y+blockSize, 0);
    }
    @Override
    public void render3D(double x, double y, double z, double blockSize){
        Core.applyColor(Core.theme.getRed(), .125f);
        VRCore.drawCube(x, y, z, x+blockSize, y+blockSize, z+blockSize, 0);
    }
    @Override
    public String getTooltip(){
        return "Block is invalid";
    }
}
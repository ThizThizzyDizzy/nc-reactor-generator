package net.ncplanner.plannerator.multiblock.editor.decal;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.planner.Core;
public class BlockInvalidDecal extends Decal{
    public BlockInvalidDecal(int x, int y, int z){
        super(x, y, z);
    }
    @Override
    public void render(Renderer renderer, float x, float y, float blockSize){
        renderer.setColor(Core.theme.getDecalColorBlockInvalid(), .125f);
        renderer.fillRect(x, y, x+blockSize, y+blockSize);
    }
    @Override
    public void render3D(Renderer renderer, float x, float y, float z, float blockSize){
        renderer.setColor(Core.theme.getDecalColorBlockInvalid(), .125f);
        renderer.drawCube(x, y, z, x+blockSize, y+blockSize, z+blockSize, null);
    }
    @Override
    public String getTooltip(){
        return "Block is invalid";
    }
}
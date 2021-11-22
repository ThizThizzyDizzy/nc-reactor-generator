package net.ncplanner.plannerator.multiblock.editor.decal;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.planner.Core;
public class MissingBladeDecal extends Decal{
    public MissingBladeDecal(int x, int y, int z){
        super(x, y, z);
    }
    @Override
    public void render(Renderer renderer, float x, float y, float blockSize){
        renderer.setColor(Core.theme.getDecalColorMissingBlade());
        renderer.fillRect(x+blockSize*.375f, y+blockSize*.375f, x+blockSize*.625f, y+blockSize*.625f);
    }
    @Override
    public void render3D(Renderer renderer, float x, float y, float z, float blockSize){
        renderer.setColor(Core.theme.getDecalColorMissingBlade());
        renderer.drawCube(x+blockSize*.375f, y+blockSize*.375f, z+blockSize*.375f, x+blockSize*.625f, y+blockSize*.625f, z+blockSize*.625f, null);
    }
    @Override
    public String getTooltip(){
        return "Missing blade";
    }
}
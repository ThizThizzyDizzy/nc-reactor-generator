package net.ncplanner.plannerator.multiblock.editor.decal;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.planner.Core;
public class UnderhaulModeratorLineDecal extends Decal{
    private final Axis axis;
    public UnderhaulModeratorLineDecal(int x, int y, int z, Axis axis){
        super(x, y, z);
        this.axis = axis;
    }
    @Override
    public void render(Renderer renderer, float x, float y, float blockSize){
        renderer.setColor(Core.theme.getDecalColorUnderhaulModeratorLine());
        switch(axis){
            case X:
                renderer.fillRect(x, y+blockSize*.375f, x+blockSize, y+blockSize*.625f);
                break;
            case Y:
                renderer.fillRect(x+blockSize*.375f, y+blockSize*.375f, x+blockSize*.625f, y+blockSize*.625f);
                break;
            case Z:
                renderer.fillRect(x+blockSize*.375f, y, x+blockSize*.625f, y+blockSize);
                break;
        }
    }
    @Override
    public void render3D(Renderer renderer, float x, float y, float z, float blockSize){
        //TODO VR DECAL: adjacent moderator
    }
    @Override
    public String getTooltip(){
        return "Valid moderator line ("+axis.toString()+")";
    }
}
package net.ncplanner.plannerator.multiblock.editor.decal;
import net.ncplanner.plannerator.Renderer;
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
    public void render(Renderer renderer, double x, double y, double blockSize){
        renderer.setColor(Core.theme.getDecalColorUnderhaulModeratorLine());
        switch(axis){
            case X:
                renderer.fillRect(x, y+blockSize*.375, x+blockSize, y+blockSize*.625);
                break;
            case Y:
                renderer.fillRect(x+blockSize*.375, y+blockSize*.375, x+blockSize*.625, y+blockSize*.625);
                break;
            case Z:
                renderer.fillRect(x+blockSize*.375, y, x+blockSize*.625, y+blockSize);
                break;
        }
    }
    @Override
    public void render3D(Renderer renderer, double x, double y, double z, double blockSize){
        //TODO VR DECAL: adjacent moderator
    }
    @Override
    public String getTooltip(){
        return "Valid moderator line ("+axis.toString()+")";
    }
}
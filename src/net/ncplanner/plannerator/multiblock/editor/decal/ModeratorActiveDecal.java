package net.ncplanner.plannerator.multiblock.editor.decal;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.planner.Core;
public class ModeratorActiveDecal extends Decal{
    private final Direction cellDirection;
    public ModeratorActiveDecal(int x, int y, int z, Direction cellDirection){
        super(x, y, z);
        this.cellDirection = cellDirection;
    }
    @Override
    public void render(Renderer renderer, double x, double y, double blockSize){
        renderer.setColor(Core.theme.getDecalColorModeratorActive());
        switch(cellDirection){
            case NX:
                renderer.fillRect(x, y+blockSize*.25, x+blockSize*.1875, y+blockSize*.75);
                break;
            case NY:
                renderer.fillRect(x+blockSize*.25, y+blockSize*.25, x+blockSize*.375, y+blockSize*.375);//top left
                renderer.fillRect(x+blockSize*.25, y+blockSize*.625, x+blockSize*.375, y+blockSize*.75);//bottom left
                renderer.fillRect(x+blockSize*.625, y+blockSize*.625, x+blockSize*.75, y+blockSize*.75);//bottom right
                renderer.fillRect(x+blockSize*.625, y+blockSize*.25, x+blockSize*.75, y+blockSize*.375);//top right
                break;
            case NZ:
                renderer.fillRect(x+blockSize*.25, y, x+blockSize*.75, y+blockSize*.1875);
                break;
            case PX:
                renderer.fillRect(x+blockSize*.8125, y+blockSize*.25, x+blockSize, y+blockSize*.75);
                break;
            case PY:
                renderer.fillRect(x+blockSize*.375, y+blockSize*.25, x+blockSize*.625, y+blockSize*.375);//top
                renderer.fillRect(x+blockSize*.25, y+blockSize*.375, x+blockSize*.375, y+blockSize*.625);//left
                renderer.fillRect(x+blockSize*.625, y+blockSize*.375, x+blockSize*.75, y+blockSize*.625);//right
                renderer.fillRect(x+blockSize*.375, y+blockSize*.625, x+blockSize*.625, y+blockSize*.75);//bottom
                break;
            case PZ:
                renderer.fillRect(x+blockSize*.25, y+blockSize*.8125, x+blockSize*.75, y+blockSize);
                break;
        }
    }
    @Override
    public void render3D(Renderer renderer, double x, double y, double z, double blockSize){
        //TODO VR DECAL: moderator active
    }
    @Override
    public String getTooltip(){
        return "Moderator Active ("+cellDirection.toString()+")";
    }
}
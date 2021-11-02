package net.ncplanner.plannerator.multiblock.editor.decal;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.planner.Core;
public class AdjacentModeratorLineDecal extends Decal{
    private final Direction direction;
    private final float efficiency;
    public AdjacentModeratorLineDecal(int x, int y, int z, Direction direction, float efficiency){
        super(x, y, z);
        this.direction = direction;
        this.efficiency = efficiency;
    }
    @Override
    public void render(Renderer renderer, double x, double y, double blockSize){
        renderer.setColor(Core.theme.getDecalColorAdjacentModeratorLine(efficiency));
        switch(direction){
            case NX:
                renderer.fillRect(x, y+blockSize*.125, x+blockSize*.125, y+blockSize*.875);
                break;
            case NY:
                renderer.fillRect(x+blockSize*.25, y+blockSize*.25, x+blockSize*.375, y+blockSize*.375);//top left
                renderer.fillRect(x+blockSize*.25, y+blockSize*.625, x+blockSize*.375, y+blockSize*.75);//bottom left
                renderer.fillRect(x+blockSize*.625, y+blockSize*.625, x+blockSize*.75, y+blockSize*.75);//bottom right
                renderer.fillRect(x+blockSize*.625, y+blockSize*.25, x+blockSize*.75, y+blockSize*.375);//top right
                break;
            case NZ:
                renderer.fillRect(x+blockSize*.125, y, x+blockSize*.875, y+blockSize*.125);
                break;
            case PX:
                renderer.fillRect(x+blockSize*.875, y+blockSize*.125, x+blockSize, y+blockSize*.875);
                break;
            case PY:
                renderer.fillRect(x+blockSize*.375, y+blockSize*.25, x+blockSize*.625, y+blockSize*.375);//top
                renderer.fillRect(x+blockSize*.25, y+blockSize*.375, x+blockSize*.375, y+blockSize*.625);//left
                renderer.fillRect(x+blockSize*.625, y+blockSize*.375, x+blockSize*.75, y+blockSize*.625);//right
                renderer.fillRect(x+blockSize*.375, y+blockSize*.625, x+blockSize*.625, y+blockSize*.75);//bottom
                break;
            case PZ:
                renderer.fillRect(x+blockSize*.125, y+blockSize*.875, x+blockSize*.875, y+blockSize);
                break;
        }
    }
    @Override
    public void render3D(Renderer renderer, double x, double y, double z, double blockSize){
        //TODO VR DECAL: adjacent moderator line
    }
    @Override
    public String getTooltip(){
        return "Adjacent moderator line ("+direction.toString()+")";
    }
}
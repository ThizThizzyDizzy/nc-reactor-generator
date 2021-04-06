package multiblock.decal;
import multiblock.Decal;
import multiblock.Direction;
import planner.Core;
public class ModeratorActiveDecal extends Decal{
    private final Direction cellDirection;
    public ModeratorActiveDecal(int x, int y, int z, Direction cellDirection){
        super(x, y, z);
        this.cellDirection = cellDirection;
    }
    @Override
    public void render(double x, double y, double blockSize){
        Core.applyColor(Core.theme.getGreen());
        switch(cellDirection){
            case NX:
                drawRect(x, y+blockSize*.25, x+blockSize*.1875, y+blockSize*.75, 0);
                break;
            case NY:
                drawRect(x+blockSize*.25, y+blockSize*.25, x+blockSize*.375, y+blockSize*.375, 0);//top left
                drawRect(x+blockSize*.25, y+blockSize*.625, x+blockSize*.375, y+blockSize*.75, 0);//bottom left
                drawRect(x+blockSize*.625, y+blockSize*.625, x+blockSize*.75, y+blockSize*.75, 0);//bottom right
                drawRect(x+blockSize*.625, y+blockSize*.25, x+blockSize*.75, y+blockSize*.375, 0);//top right
                break;
            case NZ:
                drawRect(x+blockSize*.25, y, x+blockSize*.75, y+blockSize*.1875, 0);
                break;
            case PX:
                drawRect(x+blockSize*.8125, y+blockSize*.25, x+blockSize, y+blockSize*.75, 0);
                break;
            case PY:
                drawRect(x+blockSize*.375, y+blockSize*.25, x+blockSize*.625, y+blockSize*.375, 0);//top
                drawRect(x+blockSize*.25, y+blockSize*.375, x+blockSize*.375, y+blockSize*.625, 0);//left
                drawRect(x+blockSize*.625, y+blockSize*.375, x+blockSize*.75, y+blockSize*.625, 0);//right
                drawRect(x+blockSize*.375, y+blockSize*.625, x+blockSize*.625, y+blockSize*.75, 0);//bottom
                break;
            case PZ:
                drawRect(x+blockSize*.25, y+blockSize*.8125, x+blockSize*.75, y+blockSize, 0);
                break;
        }
    }
    @Override
    public void render3D(double x, double y, double z, double blockSize){
        //TODO VR DECAL: moderator active
    }
    @Override
    public String getTooltip(){
        return "Moderator Active ("+cellDirection.toString()+")";
    }
}
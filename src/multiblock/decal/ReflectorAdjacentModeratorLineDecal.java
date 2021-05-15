package multiblock.decal;
import multiblock.Decal;
import multiblock.Direction;
import planner.Core;
public class ReflectorAdjacentModeratorLineDecal extends Decal{
    private final Direction direction;
    public ReflectorAdjacentModeratorLineDecal(int x, int y, int z, Direction direction){
        super(x, y, z);
        this.direction = direction;
    }
    @Override
    public void render(double x, double y, double blockSize){
        Core.applyColor(Core.theme.getDecalColorReflectorAdjacentModeratorLine());
        switch(direction){
            case NX:
                drawRect(x, y+blockSize*.125, x+blockSize*.125, y+blockSize*.875, 0);
                break;
            case NY:
                drawRect(x+blockSize*.25, y+blockSize*.25, x+blockSize*.375, y+blockSize*.375, 0);//top left
                drawRect(x+blockSize*.25, y+blockSize*.625, x+blockSize*.375, y+blockSize*.75, 0);//bottom left
                drawRect(x+blockSize*.625, y+blockSize*.625, x+blockSize*.75, y+blockSize*.75, 0);//bottom right
                drawRect(x+blockSize*.625, y+blockSize*.25, x+blockSize*.75, y+blockSize*.375, 0);//top right
                break;
//                drawRect(x+blockSize*.125, y+blockSize*.125, x+blockSize*.375, y+blockSize*.375, 0);//top left
//                drawRect(x+blockSize*.125, y+blockSize*.625, x+blockSize*.375, y+blockSize*.875, 0);//bottom left
//                drawRect(x+blockSize*.625, y+blockSize*.625, x+blockSize*.875, y+blockSize*.875, 0);//bottom right
//                drawRect(x+blockSize*.625, y+blockSize*.125, x+blockSize*.875, y+blockSize*.375, 0);//top right
//                break;
            case NZ:
                drawRect(x+blockSize*.125, y, x+blockSize*.875, y+blockSize*.125, 0);
                break;
            case PX:
                drawRect(x+blockSize*.875, y+blockSize*.125, x+blockSize, y+blockSize*.875, 0);
                break;
            case PY:
                drawRect(x+blockSize*.375, y+blockSize*.25, x+blockSize*.625, y+blockSize*.375, 0);//top
                drawRect(x+blockSize*.25, y+blockSize*.375, x+blockSize*.375, y+blockSize*.625, 0);//left
                drawRect(x+blockSize*.625, y+blockSize*.375, x+blockSize*.75, y+blockSize*.625, 0);//right
                drawRect(x+blockSize*.375, y+blockSize*.625, x+blockSize*.625, y+blockSize*.75, 0);//bottom
                break;
//                drawRect(x+blockSize*.375, y+blockSize*.125, x+blockSize*.625, y+blockSize*.375, 0);//top
//                drawRect(x+blockSize*.125, y+blockSize*.375, x+blockSize*.375, y+blockSize*.625, 0);//left
//                drawRect(x+blockSize*.625, y+blockSize*.375, x+blockSize*.875, y+blockSize*.625, 0);//right
//                drawRect(x+blockSize*.375, y+blockSize*.625, x+blockSize*.625, y+blockSize*.875, 0);//bottom
//                break;
            case PZ:
                drawRect(x+blockSize*.125, y+blockSize*.875, x+blockSize*.875, y+blockSize, 0);
                break;
        }
    }
    @Override
    public void render3D(double x, double y, double z, double blockSize){
        //TODO VR DECAL: adjacent moderator line
    }
    @Override
    public String getTooltip(){
        return "Adjacent moderator line ("+direction.toString()+")";
    }
}
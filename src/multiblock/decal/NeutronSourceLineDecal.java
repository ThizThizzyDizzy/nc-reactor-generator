package multiblock.decal;
import multiblock.Decal;
import multiblock.Direction;
import planner.Core;
public class NeutronSourceLineDecal extends Decal{
    private final Direction direction;
    public NeutronSourceLineDecal(int x, int y, int z, Direction direction){
        super(x, y, z);
        this.direction = direction;
    }
    @Override
    public void render(double x, double y, double blockSize){
        Core.applyColor(Core.theme.getRGBA(1, 0.5f, 0, 1));
        switch(direction){
            case PX:
            case NX:
                drawRect(x, y+blockSize*.375, x+blockSize, y+blockSize*.625, 0);
                break;
            case PY:
            case NY:
                drawRect(x+blockSize*.375, y+blockSize*.375, x+blockSize*.625, y+blockSize*.625, 0);
                break;
            case PZ:
            case NZ:
                drawRect(x+blockSize*.375, y, x+blockSize*.625, y+blockSize, 0);
                break;
        }
    }
    @Override
    public void render3D(double x, double y, double z, double blockSize){
        //TODO VR DECAL: adjacent moderator
    }
    @Override
    public String getTooltip(){
        return "Neutron source path ("+direction.toString()+")";
    }
}
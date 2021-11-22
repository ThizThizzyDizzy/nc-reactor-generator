package net.ncplanner.plannerator.multiblock.editor.decal;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.planner.Core;
public class NeutronSourceTargetDecal extends Decal{
    private final Direction cellDirection;
    public NeutronSourceTargetDecal(int x, int y, int z, Direction cellDirection){
        super(x, y, z);
        this.cellDirection = cellDirection;
    }
    @Override
    public void render(Renderer renderer, float x, float y, float blockSize){
        renderer.setColor(Core.theme.getDecalColorNeutronSourceTarget());
        switch(cellDirection){
            case NX:
                renderer.fillRect(x, y+blockSize*.25f, x+blockSize*.125f, y+blockSize*.75f);
                break;
            case NY:
                renderer.fillRect(x+blockSize*.25f, y+blockSize*.25f, x+blockSize*.375f, y+blockSize*.375f);//top left
                renderer.fillRect(x+blockSize*.25f, y+blockSize*.625f, x+blockSize*.375f, y+blockSize*.75f);//bottom left
                renderer.fillRect(x+blockSize*.625f, y+blockSize*.625f, x+blockSize*.75f, y+blockSize*.75f);//bottom right
                renderer.fillRect(x+blockSize*.625f, y+blockSize*.25f, x+blockSize*.75f, y+blockSize*.375f);//top right
                break;
            case NZ:
                renderer.fillRect(x+blockSize*.25f, y, x+blockSize*.75f, y+blockSize*.125f);
                break;
            case PX:
                renderer.fillRect(x+blockSize*.875f, y+blockSize*.25f, x+blockSize, y+blockSize*.75f);
                break;
            case PY:
                renderer.fillRect(x+blockSize*.375f, y+blockSize*.25f, x+blockSize*.625f, y+blockSize*.375f);//top
                renderer.fillRect(x+blockSize*.25f, y+blockSize*.375f, x+blockSize*.375f, y+blockSize*.625f);//left
                renderer.fillRect(x+blockSize*.625f, y+blockSize*.375f, x+blockSize*.75f, y+blockSize*.625f);//right
                renderer.fillRect(x+blockSize*.375f, y+blockSize*.625f, x+blockSize*.625f, y+blockSize*.75f);//bottom
                break;
            case PZ:
                renderer.fillRect(x+blockSize*.25f, y+blockSize*.875f, x+blockSize*.75f, y+blockSize);
                break;
        }
    }
    @Override
    public void render3D(Renderer renderer, float x, float y, float z, float blockSize){
        //TODO VR DECAL: neutron source
    }
    @Override
    public String getTooltip(){
        return "Targeted block ("+cellDirection.toString()+")";
    }
}
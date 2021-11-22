package net.ncplanner.plannerator.multiblock.editor.decal;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.planner.Core;
public class NeutronSourceLineDecal extends Decal{
    private final Direction direction;
    public NeutronSourceLineDecal(int x, int y, int z, Direction direction){
        super(x, y, z);
        this.direction = direction;
    }
    @Override
    public void render(Renderer renderer, float x, float y, float blockSize){
        renderer.setColor(Core.theme.getDecalColorNeutronSourceLine());
        switch(direction){
            case PX:
            case NX:
                renderer.fillRect(x, y+blockSize*.375f, x+blockSize, y+blockSize*.625f);
                break;
            case PY:
            case NY:
                renderer.fillRect(x+blockSize*.375f, y+blockSize*.375f, x+blockSize*.625f, y+blockSize*.625f);
                break;
            case PZ:
            case NZ:
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
        return "Neutron source path ("+direction.toString()+")";
    }
}
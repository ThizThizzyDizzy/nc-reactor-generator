package net.ncplanner.plannerator.multiblock.editor.decal;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.planner.Core;
public class OverhaulModeratorLineDecal extends Decal{
    private final Direction direction;
    private final int flux;
    private final float efficiency;
    public OverhaulModeratorLineDecal(int x, int y, int z, Direction direction, int flux, float efficiency){
        super(x, y, z);
        this.direction = direction;
        this.flux = flux;
        this.efficiency = efficiency;
    }
    @Override
    public void render(Renderer renderer, float x, float y, float blockSize){
        renderer.setColor(Core.theme.getDecalColorOverhaulModeratorLine(efficiency));
        renderer.setFont(Core.theme.getDecalFont());
        switch(direction){
            case PX:
                renderer.fillRect(x, y+blockSize*.55f, x+blockSize, y+blockSize*.75f);
                break;
            case NX:
                renderer.fillRect(x, y+blockSize*.25f, x+blockSize, y+blockSize*.45f);
                break;
            case PY:
                renderer.fillRect(x+blockSize*.55f, y+blockSize*.25f, x+blockSize*.75f, y+blockSize*.45f);
                break;
            case NY:
                renderer.fillRect(x+blockSize*.25f, y+blockSize*.55f, x+blockSize*.45f, y+blockSize*.75f);
                break;
            case PZ:
                renderer.fillRect(x+blockSize*.25f, y, x+blockSize*.45f, y+blockSize);
                break;
            case NZ:
                renderer.fillRect(x+blockSize*.55f, y, x+blockSize*.75f, y+blockSize);
                break;
        }
        renderer.setColor(Core.theme.getDecalTextColor());
        switch(direction){
            case PX:
                renderer.drawCenteredText(-blockSize+x, y+blockSize*.55f, blockSize+x+blockSize, y+blockSize*.75f, flux+"");
                break;
            case NX:
                renderer.drawCenteredText(-blockSize+x, y+blockSize*.25f, blockSize+x+blockSize, y+blockSize*.45f, flux+"");
                break;
            case PY:
                renderer.drawCenteredText(-blockSize+x+blockSize*.55f, y+blockSize*.25f, blockSize+x+blockSize*.75f, y+blockSize*.45f, flux+"");
                break;
            case NY:
                renderer.drawCenteredText(-blockSize+x+blockSize*.25f, y+blockSize*.55f, blockSize+x+blockSize*.45f, y+blockSize*.75f, flux+"");
                break;
            case PZ:
                renderer.drawCenteredText(-blockSize+x+blockSize*.25f, y+blockSize*.4f, blockSize+x+blockSize*.45f, y+blockSize*.6f, flux+"");
                break;
            case NZ:
                renderer.drawCenteredText(-blockSize+x+blockSize*.55f, y+blockSize*.4f, blockSize+x+blockSize*.75f, y+blockSize*.6f, flux+"");
                break;
        }
        renderer.resetFont();
    }
    @Override
    public void render3D(Renderer renderer, float x, float y, float z, float blockSize){
        //TODO VR DECAL: adjacent moderator
    }
    @Override
    public String getTooltip(){
        return flux+" flux "+direction.toString()+", Eff: "+efficiency;
    }
}
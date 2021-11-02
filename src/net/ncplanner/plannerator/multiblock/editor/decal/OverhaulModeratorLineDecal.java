package net.ncplanner.plannerator.multiblock.editor.decal;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.planner.Core;
import simplelibrary.font.FontManager;
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
    public void render(Renderer renderer, double x, double y, double blockSize){
        renderer.setColor(Core.theme.getDecalColorOverhaulModeratorLine(efficiency));
        FontManager.setFont("small");
        switch(direction){
            case PX:
                renderer.fillRect(x, y+blockSize*.55, x+blockSize, y+blockSize*.75);
                break;
            case NX:
                renderer.fillRect(x, y+blockSize*.25, x+blockSize, y+blockSize*.45);
                break;
            case PY:
                renderer.fillRect(x+blockSize*.55, y+blockSize*.25, x+blockSize*.75, y+blockSize*.45);
                break;
            case NY:
                renderer.fillRect(x+blockSize*.25, y+blockSize*.55, x+blockSize*.45, y+blockSize*.75);
                break;
            case PZ:
                renderer.fillRect(x+blockSize*.25, y, x+blockSize*.45, y+blockSize);
                break;
            case NZ:
                renderer.fillRect(x+blockSize*.55, y, x+blockSize*.75, y+blockSize);
                break;
        }
        renderer.setColor(Core.theme.getDecalTextColor());
        switch(direction){
            case PX:
                renderer.drawCenteredText(-blockSize+x, y+blockSize*.55, blockSize+x+blockSize, y+blockSize*.75, flux+"");
                break;
            case NX:
                renderer.drawCenteredText(-blockSize+x, y+blockSize*.25, blockSize+x+blockSize, y+blockSize*.45, flux+"");
                break;
            case PY:
                renderer.drawCenteredText(-blockSize+x+blockSize*.55, y+blockSize*.25, blockSize+x+blockSize*.75, y+blockSize*.45, flux+"");
                break;
            case NY:
                renderer.drawCenteredText(-blockSize+x+blockSize*.25, y+blockSize*.55, blockSize+x+blockSize*.45, y+blockSize*.75, flux+"");
                break;
            case PZ:
                renderer.drawCenteredText(-blockSize+x+blockSize*.25, y+blockSize*.4, blockSize+x+blockSize*.45, y+blockSize*.6, flux+"");
                break;
            case NZ:
                renderer.drawCenteredText(-blockSize+x+blockSize*.55, y+blockSize*.4, blockSize+x+blockSize*.75, y+blockSize*.6, flux+"");
                break;
        }
        FontManager.setFont("high resolution");
    }
    @Override
    public void render3D(Renderer renderer, double x, double y, double z, double blockSize){
        //TODO VR DECAL: adjacent moderator
    }
    @Override
    public String getTooltip(){
        return flux+" flux "+direction.toString()+", Eff: "+efficiency;
    }
}
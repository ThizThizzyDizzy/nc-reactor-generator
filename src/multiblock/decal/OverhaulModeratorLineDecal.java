package multiblock.decal;
import multiblock.Decal;
import multiblock.Direction;
import planner.Core;
import simplelibrary.font.FontManager;
public class OverhaulModeratorLineDecal extends Decal{
    private final Direction direction;
    private final int flux;
    private final float efficiency;
    private double colorPow = 3;
    public OverhaulModeratorLineDecal(int x, int y, int z, Direction direction, int flux, float efficiency){
        super(x, y, z);
        this.direction = direction;
        this.flux = flux;
        this.efficiency = efficiency;
    }
    @Override
    public void render(double x, double y, double blockSize){
        Core.applyColor(Core.theme.getRGBA(0, (float)Math.max(0,Math.min(1,Math.pow(efficiency, colorPow)/2)),1,1));
        FontManager.setFont("small");
        switch(direction){
            case PX:
                drawRect(x, y+blockSize*.55, x+blockSize, y+blockSize*.75, 0);
                break;
            case NX:
                drawRect(x, y+blockSize*.25, x+blockSize, y+blockSize*.45, 0);
                break;
            case PY:
                drawRect(x+blockSize*.55, y+blockSize*.25, x+blockSize*.75, y+blockSize*.45, 0);
                break;
            case NY:
                drawRect(x+blockSize*.25, y+blockSize*.55, x+blockSize*.45, y+blockSize*.75, 0);
                break;
            case PZ:
                drawRect(x+blockSize*.25, y, x+blockSize*.45, y+blockSize, 0);
                break;
            case NZ:
                drawRect(x+blockSize*.55, y, x+blockSize*.75, y+blockSize, 0);
                break;
        }
        Core.applyColor(Core.theme.getTextColor());
        switch(direction){
            case PX:
                drawCenteredText(-blockSize+x, y+blockSize*.55, blockSize+x+blockSize, y+blockSize*.75, flux+"");
                break;
            case NX:
                drawCenteredText(-blockSize+x, y+blockSize*.25, blockSize+x+blockSize, y+blockSize*.45, flux+"");
                break;
            case PY:
                drawCenteredText(-blockSize+x+blockSize*.55, y+blockSize*.25, blockSize+x+blockSize*.75, y+blockSize*.45, flux+"");
                break;
            case NY:
                drawCenteredText(-blockSize+x+blockSize*.25, y+blockSize*.55, blockSize+x+blockSize*.45, y+blockSize*.75, flux+"");
                break;
            case PZ:
                drawCenteredText(-blockSize+x+blockSize*.25, y+blockSize*.4, blockSize+x+blockSize*.45, y+blockSize*.6, flux+"");
                break;
            case NZ:
                drawCenteredText(-blockSize+x+blockSize*.55, y+blockSize*.4, blockSize+x+blockSize*.75, y+blockSize*.6, flux+"");
                break;
        }
        FontManager.setFont("high resolution");
    }
    @Override
    public void render3D(double x, double y, double z, double blockSize){
        //TODO VR DECAL: adjacent moderator
    }
    @Override
    public String getTooltip(){
        return flux+" flux "+direction.toString()+", Eff: "+efficiency;
    }
}
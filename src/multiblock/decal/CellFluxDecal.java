package multiblock.decal;
import multiblock.Decal;
import planner.Core;
import simplelibrary.font.FontManager;
public class CellFluxDecal extends Decal{
    private final int flux;
    private final int criticality;
    public CellFluxDecal(int x, int y, int z, int flux, int criticality){
        super(x, y, z);
        this.flux = flux;
        this.criticality = criticality;
    }
    @Override
    public void render(double x, double y, double blockSize){
        Core.applyColor(Core.theme.getDecalColorCellFlux(flux, criticality));
        drawRect(x, y+blockSize*.4, x+blockSize*Math.min(1, flux*1d/criticality), y+blockSize*.6, 0);
        Core.applyColor(Core.theme.getDecalTextColor());
        FontManager.setFont("small");
        drawCenteredText(x, y+blockSize*.4, x+blockSize, y+blockSize*.6, flux+"/"+criticality);
        FontManager.setFont("high resolution");
    }
    @Override
    public void render3D(double x, double y, double z, double blockSize){
        //TODO VR DECAL: adjacent moderator line
    }
    @Override
    public String getTooltip(){
        return "Cell flux: "+flux+"/"+criticality;
    }
}
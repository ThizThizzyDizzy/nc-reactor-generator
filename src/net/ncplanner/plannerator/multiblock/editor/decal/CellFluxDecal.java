package net.ncplanner.plannerator.multiblock.editor.decal;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.planner.Core;
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
    public void render(Renderer renderer, double x, double y, double blockSize){
        renderer.setColor(Core.theme.getDecalColorCellFlux(flux, criticality));
        renderer.fillRect(x, y+blockSize*.4, x+blockSize*Math.min(1, flux*1d/criticality), y+blockSize*.6);
        renderer.setColor(Core.theme.getDecalTextColor());
        FontManager.setFont("small");
        renderer.drawCenteredText(x, y+blockSize*.4, x+blockSize, y+blockSize*.6, flux+"/"+criticality);
        FontManager.setFont("high resolution");
    }
    @Override
    public void render3D(Renderer renderer, double x, double y, double z, double blockSize){
        //TODO VR DECAL: adjacent moderator line
    }
    @Override
    public String getTooltip(){
        return "Cell flux: "+flux+"/"+criticality;
    }
}
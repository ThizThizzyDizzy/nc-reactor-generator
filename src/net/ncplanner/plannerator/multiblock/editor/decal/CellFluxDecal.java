package net.ncplanner.plannerator.multiblock.editor.decal;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.planner.Core;
public class CellFluxDecal extends Decal{
    public final int flux;
    public final int criticality;
    public CellFluxDecal(int x, int y, int z, int flux, int criticality){
        super(x, y, z);
        this.flux = flux;
        this.criticality = criticality;
    }
    @Override
    public void render(Renderer renderer, float x, float y, float blockSize){
        renderer.setColor(Core.theme.getDecalColorCellFlux(flux, criticality));
        renderer.fillRect(x, y+blockSize*.4f, x+blockSize*Math.min(1, flux*1f/criticality), y+blockSize*.6f);
        renderer.setColor(Core.theme.getDecalTextColor());
        renderer.setFont(Core.theme.getDecalFont());
        renderer.drawCenteredText(x, y+blockSize*.4f, x+blockSize, y+blockSize*.6f, flux+"/"+criticality);
        renderer.resetFont();
    }
    @Override
    public void render3D(Renderer renderer, float x, float y, float z, float blockSize){
        //TODO VR DECAL: adjacent moderator line
    }
    @Override
    public String getTooltip(){
        return "Cell flux: "+flux+"/"+criticality;
    }
}
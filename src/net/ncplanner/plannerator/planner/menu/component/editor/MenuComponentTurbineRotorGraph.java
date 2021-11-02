package net.ncplanner.plannerator.planner.menu.component.editor;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.planner.menu.MenuEdit;
import org.lwjgl.opengl.GL11;
import simplelibrary.font.FontManager;
import simplelibrary.image.Color;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentTurbineRotorGraph extends MenuComponent{
    private final OverhaulTurbine turbine;
    private final int blockSize;
    private final MenuEdit editor;
    public MenuComponentTurbineRotorGraph(int x, int y, int blockSize, MenuEdit editor, OverhaulTurbine turbine){
        super(x, y, turbine.getInternalDepth()*blockSize, blockSize*6);
        this.turbine = turbine;
        this.blockSize = blockSize;
        this.editor = editor;
    }
    @Override
    public void render(){
        Renderer renderer = new Renderer();
        if(turbine.rotorValid){
            renderer.setWhite();
            drawRect(x, y, x+width, y+blockSize*4, editor.turbineGraph.getTexture());
            double wideScale = 1;
            double len = FontManager.getLengthForStringWithHeight("Actual Expansion", blockSize/2);
            wideScale = Math.min(wideScale, width/len);
            drawText(x, y+blockSize*4.5, x+width, y+blockSize*(4.5+wideScale/2), "Actual Expansion");
            renderer.setColor(new Color(31,63,255));
            drawText(x, y+blockSize*4, x+width, y+blockSize*(4+wideScale/2), "Ideal Expansion");
            double blockWidth = width/10;
            float tint = .75f;
            for(int i = 0; i<10; i++){
                int I = 9-i;
                String text = ">"+I*10+"%";
                double scale = 1;
                double slen = FontManager.getLengthForStringWithHeight(text.length()==2?"0"+text:text, blockSize)+1;
                scale = Math.min(scale, blockWidth/slen);
                if(scale<.25){
                    text = I*10+"%";
                    scale = 1;
                    slen = FontManager.getLengthForStringWithHeight(text.length()==2?"0"+text:text, blockSize)+1;
                    scale = Math.min(scale, blockWidth/slen);
                }
                float eff = I/10f;
                GL11.glColor4d(tint*Math.max(0,Math.min(1,-Math.abs(3*eff-1.5)+1.5)), tint*Math.max(0,Math.min(1,3*eff-1)), 0, 1);
                drawRect(x+i*blockWidth, y+blockSize*5, x+(i+1)*blockWidth, y+blockSize*6, 0);
                renderer.setWhite();
                drawText(x+i*blockWidth, y+blockSize*5, x+(i+1)*blockWidth, y+blockSize*(5+scale), text);
            }
        }
    }
}
package net.ncplanner.plannerator.planner.gui.menu.component.editor;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.gui.Component;
public class MenuComponentTurbineRotorGraph extends Component{
    private final OverhaulTurbine turbine;
    private final int footerHeight;
    public MenuComponentTurbineRotorGraph(int x, int y, int width, int height, int footerHeight, OverhaulTurbine turbine){
        super(x, y, width, height);
        this.turbine = turbine;
        this.footerHeight = footerHeight;
    }
    @Override
    public void draw(double deltaTime){
        if(turbine==null)return;
        Renderer renderer = new Renderer();
        if(turbine.rotorValid){
            renderer.setWhite();
            {
                float width = this.width;
                float height = this.height-footerHeight*2;
                if(turbine.rotorValid){
                    float max = 0;
                    float min = Float.MAX_VALUE;
                    for(double d : turbine.idealExpansion){
                        max = (float)Math.max(max,d);
                        min = (float)Math.min(min,d);
                    }
                    for(double d : turbine.actualExpansion){
                        max = (float)Math.max(max,d);
                        min = (float)Math.min(min,d);
                    }
                    renderer.translate(x, y);
                    renderer.bound(0, 0, width, height);
                    renderer.unbindTexture();
                    renderer.setColor(0, 0, 0, 1);
                    renderer.fillRect(0, 0, width, height);
                    float tint = .75f;
                    for(float eff = 0; eff<=1; eff+=.1){
                        renderer.setColor(tint*Math.max(0,Math.min(1,-Math.abs(3*eff-1.5f)+1.5f)), tint*Math.max(0,Math.min(1,3*eff-1)), 0, 1);
                        for(int i = 1; i<turbine.idealExpansion.length; i++){
                            float prevLowerBound = (float)Math.min(turbine.idealExpansion[i-1]*eff,turbine.idealExpansion[i-1]/eff);
                            float prevUpperBound = (float)Math.max(turbine.idealExpansion[i-1]*eff,turbine.idealExpansion[i-1]/eff);
                            float lowerBound = (float)Math.min(turbine.idealExpansion[i]*eff,turbine.idealExpansion[i]/eff);
                            float upperBound = (float)Math.max(turbine.idealExpansion[i]*eff,turbine.idealExpansion[i]/eff);
                            if(!Double.isFinite(prevLowerBound))prevLowerBound = min;
                            if(!Double.isFinite(lowerBound))lowerBound = min;
                            if(!Double.isFinite(prevUpperBound))prevUpperBound = max;
                            if(!Double.isFinite(upperBound))upperBound = max;
                            renderer.fillQuad((i-1)*width/(turbine.idealExpansion.length-1), height-height*((prevUpperBound-min)/(max-min)),
                                    (i-1)*width/(turbine.idealExpansion.length-1), height-height*((prevLowerBound-min)/(max-min)),
                                    (i)*width/(turbine.idealExpansion.length-1), height-height*((upperBound-min)/(max-min)),
                                    (i)*width/(turbine.idealExpansion.length-1), height-height*((lowerBound-min)/(max-min)));
                        }
                    }
                    renderer.setColor(0, 0, 1, 1);
                    int thickness = 3;
                    for(int i = 1; i<turbine.idealExpansion.length; i++){
                        renderer.fillQuad((i-1)*width/(turbine.idealExpansion.length-1), height-height*(((float)turbine.idealExpansion[i-1]-min)/(max-min)),
                                (i-1)*width/(turbine.idealExpansion.length-1), height+thickness-height*(((float)turbine.idealExpansion[i-1]-min)/(max-min)),
                                (i)*width/(turbine.idealExpansion.length-1), height-height*(((float)turbine.idealExpansion[i]-min)/(max-min)),
                                (i)*width/(turbine.idealExpansion.length-1), height+thickness-height*(((float)turbine.idealExpansion[i]-min)/(max-min)));
                    }
                    renderer.setColor(1, 1, 1, 1);
                    for(int i = 1; i<turbine.actualExpansion.length; i++){
                        renderer.fillQuad((i-1)*width/(turbine.idealExpansion.length-1), height-height*(((float)turbine.actualExpansion[i-1]-min)/(max-min)),
                                (i-1)*width/(turbine.idealExpansion.length-1), height+thickness-height*(((float)turbine.actualExpansion[i-1]-min)/(max-min)),
                                (i)*width/(turbine.idealExpansion.length-1), height-height*(((float)turbine.actualExpansion[i]-min)/(max-min)),
                                (i)*width/(turbine.idealExpansion.length-1), height+thickness-height*(((float)turbine.actualExpansion[i]-min)/(max-min)));
                    }
                    renderer.unBound();
                    renderer.unTranslate();
                    renderer.setWhite();
                }
            }
            float wideScale = 1;
            float len = renderer.getStringWidth("Actual Expansion", footerHeight);
            wideScale = MathUtil.min(wideScale, (width/2)/len);
            renderer.drawCenteredText(x, y+height-footerHeight*2, x+width/2, y+height-footerHeight*2f+footerHeight*wideScale, "Actual Expansion");
            renderer.setColor(new Color(31,63,255));
            renderer.drawCenteredText(x+width/2, y+height-footerHeight*2, x+width, y+height-footerHeight*2+footerHeight*wideScale, "Ideal Expansion");
            float blockWidth = width/10;
            float tint = .75f;
            for(int i = 0; i<10; i++){
                int I = 9-i;
                String text = ">"+I*10+"%";
                float scale = 1;
                float slen = renderer.getStringWidth(text.length()==2?"0"+text:text, footerHeight)+1;
                scale = MathUtil.min(scale, blockWidth/slen);
                if(scale<.25){
                    text = I*10+"%";
                    scale = 1;
                    slen = renderer.getStringWidth(text.length()==2?"0"+text:text, footerHeight)+1;
                    scale = MathUtil.min(scale, blockWidth/slen);
                }
                float eff = I/10f;
                renderer.setColor(tint*MathUtil.max(0,MathUtil.min(1,-MathUtil.abs(3*eff-1.5f)+1.5f)), tint*MathUtil.max(0,MathUtil.min(1,3*eff-1)), 0, 1);
                renderer.fillRect(x+i*blockWidth, y+height-footerHeight, x+(i+1)*blockWidth, y+height);
                renderer.setWhite();
                renderer.drawText(x+i*blockWidth, y+height-footerHeight, x+(i+1)*blockWidth, y+height-footerHeight+footerHeight*scale, text);
            }
        }
    }
}
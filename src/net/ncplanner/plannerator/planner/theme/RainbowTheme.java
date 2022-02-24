package net.ncplanner.plannerator.planner.theme;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.theme.legacy.SolidColorTheme;
public class RainbowTheme extends ChangingColorTheme{
    private float hue;
    private final float saturation;
    private final float brightness;
    public RainbowTheme(String name){
        this(name, 0, 0.25f, 1, 1);
    }
    public RainbowTheme(String name, float initalHue, float changeSpeed, float saturation, float brightness){
        super(name, () -> {
            return null;
        });
        this.hue = initalHue;
        Thread t = new Thread(() -> {
            while(true){
                current = new SolidColorTheme(name, getColor());
                hue+=changeSpeed/360;
                if(hue>1)hue--;
                try{
                    Thread.sleep(10);
                }catch(InterruptedException ex){
                    Logger.getLogger(RainbowTheme.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, name+" Theme");
        t.setDaemon(true);
        t.start();
        this.saturation = saturation;
        this.brightness = brightness;
    }
    @Override
    public void onSet(){}
    private Color getColor(){
        return Color.fromHSB(hue, saturation, brightness);
    }
    @Override
    public boolean shouldContantlyUpdateBackground(){
        return true;
    }
}
package planner.theme;
import java.util.logging.Level;
import java.util.logging.Logger;
import planner.theme.legacy.SolidColorTheme;
import simplelibrary.image.Color;
public class RainbowTheme extends ChangingTheme{
    private float hue;
    public RainbowTheme(String name){
        super(name, () -> {
            return null;
        });
        Thread t = new Thread(() -> {
            while(true){
                current = new SolidColorTheme(name, getColor());
                hue+=0.25f/360;
                if(hue>1)hue--;
                try{
                    Thread.sleep(10);
                }catch(InterruptedException ex){
                    Logger.getLogger(RainbowTheme.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
    @Override
    public void onSet(){}
    private Color getColor(){
        return Color.fromHSB(hue, 1, 1);
    }
    @Override
    public boolean shouldContantlyUpdateBackground(){
        return true;
    }
}
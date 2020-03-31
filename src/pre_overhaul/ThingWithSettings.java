package pre_overhaul;
import java.awt.Component;
import javax.swing.JPanel;
public class ThingWithSettings{//I got tired of copying Settings code between GenerationPlan and GenerationModel
    public final Setting[] settings;
    public ThingWithSettings(Setting... settings){
        this.settings = settings;
    }
    public void fillSettings(JPanel panel){
        for(Setting s : settings){
            for(Component comp : s.createComponents(panel.getWidth())){
                int maxY = 0;
                for(Component c : panel.getComponents()){
                    maxY = Math.max(maxY, c.getY()+c.getHeight());
                }
                panel.add(comp);
                comp.setLocation(0, maxY);
            }
        }
    }
    public Object getSetting(String name){
        for(Setting s : settings){
            if(s.name.equalsIgnoreCase(name)){
                return s.value;
            }
        }
        for(Setting s : settings){
            if(s.name.toLowerCase().contains(name.toLowerCase())){
                return s.value;
            }
        }
        return null;
    }
}

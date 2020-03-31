package pre_overhaul;
import javax.swing.JComponent;
import javax.swing.JLabel;
public abstract class Setting<E>{
    public E value;
    protected final String name;
    public Setting(String name, E defaultValue){
        value = defaultValue;
        this.name = name;
    }
    protected JComponent createLabel(String name, int width){
        JLabel label = new JLabel(name, JLabel.CENTER);
        label.setVerticalAlignment(JLabel.BOTTOM);
        label.setSize(width, 20);
        return label;
    }
    public abstract JComponent[] createComponents(int width);
}
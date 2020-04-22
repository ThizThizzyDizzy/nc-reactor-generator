package common;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
public class SettingBoolean extends Setting<Boolean>{
    public SettingBoolean(String name, boolean defaultValue){
        super(name, defaultValue);
    }
    @Override
    public JComponent[] createComponents(int width){
        JCheckBox box = new JCheckBox();
        box.setSelected(value);
        box.addActionListener((e) -> {
            value = box.isSelected();
        });
        box.setSize(width,20);
        return new JComponent[]{createLabel(name, width), box};
    }
}
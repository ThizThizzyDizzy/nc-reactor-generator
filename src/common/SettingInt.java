package common;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
public class SettingInt extends Setting<Integer>{
    private final int min;
    private final int max;
    public SettingInt(String name, int defaultValue){
        this(name, defaultValue, Integer.MIN_VALUE);
    }
    public SettingInt(String name, int defaultValue, int min){
        this(name, defaultValue, min, Integer.MAX_VALUE);
    }
    public SettingInt(String name, int defaultValue, int min, int max){
        super(name, defaultValue);
        this.min = min;
        this.max = max;
    }
    @Override
    public JComponent[] createComponents(int width){
        JSpinner spinner = new JSpinner(new SpinnerNumberModel((int)value, min, max, 1));
        spinner.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e){
                value = (Integer) spinner.getValue();
            }
        });
        spinner.setSize(width,20);
        return new JComponent[]{createLabel(name, width), spinner};
    }
}
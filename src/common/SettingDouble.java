package common;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
public class SettingDouble extends Setting<Double>{
    private final double min;
    private final double max;
    private final double interval;
    public SettingDouble(String name, double defaultValue){
        this(name, defaultValue, Double.MIN_VALUE);
    }
    public SettingDouble(String name, double defaultValue, double min){
        this(name, defaultValue, min, Double.MAX_VALUE);
    }
    public SettingDouble(String name, double defaultValue, double min, double max){
        this(name, defaultValue, min, max, .1f);
    }
    public SettingDouble(String name, double defaultValue, double min, double max, double interval){
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.interval = interval;
    }
    @Override
    public JComponent[] createComponents(int width){
        JSpinner spinner = new JSpinner(new SpinnerNumberModel((double)value, min, max, interval));
        spinner.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e){
                value = (double) spinner.getValue();
            }
        });
        spinner.setSize(width,20);
        return new JComponent[]{createLabel(name, width), spinner};
    }
}
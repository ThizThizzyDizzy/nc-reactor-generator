package old;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

public enum Priority {
    EFFICIENCY("Maximize Efficiency"),
    POWER("Maximize RF output"),
    HEAT("Minimize Heat"),
    BREEDER("Maximize fuel usage"),
    CELL_COUNT("Maximixe cell count");
    private final String desc;
    private Priority(String desc){
        this.desc = desc;
    }
    /**
     * Compares two reactors according to this priority's rules
     * @param r1
     * @param r2
     * @return A positive number if r1 is better than r2, a negative number if it is worse, or zero of they are equal
     */
    public double compare(Reactor r1, Reactor r2){
        switch(this){
            case EFFICIENCY:
                return r1.totalEfficiency-r2.totalEfficiency;
            case HEAT:
                return r2.heat-r1.heat;
            case POWER:
                return r1.power-r2.power;
            case BREEDER:
                return r1.getFuelSpeed()-r2.getFuelSpeed();
            case CELL_COUNT:
                return r1.getFuelCells()-r2.getFuelCells();
            default:
                throw new IllegalArgumentException("Unknown priority: "+name());
        }
    }
    @Override
    public String toString() {
        return desc;
    }
    public static ComboBoxModel<String> getComboBoxModel() {
        String[] items = new String[values().length];
        Priority[] values = values();
        for(int i = 0; i<items.length; i++){
            items[i] = values[i].toString();
        }
        ComboBoxModel<String> model = new DefaultComboBoxModel<>(items);
        return model;
    }
}

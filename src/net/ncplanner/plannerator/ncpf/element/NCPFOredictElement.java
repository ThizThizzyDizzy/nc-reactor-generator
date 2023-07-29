package net.ncplanner.plannerator.ncpf.element;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFOredictElement extends NCPFElementDefinition{
    public String oredict;
    public NCPFOredictElement(){
        super("oredict");
    }
    public NCPFOredictElement(String oredict){
        this();
        this.oredict = oredict;
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        oredict = ncpf.getString("oredict");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("oredict", oredict);
    }
    @Override
    public boolean matches(NCPFElementDefinition definition){
        if(definition instanceof NCPFOredictElement){
            return oredict.equals(((NCPFOredictElement)definition).oredict);
        }
        return false;
    }
    @Override
    public String getName(){
        return oredict;
    }
}
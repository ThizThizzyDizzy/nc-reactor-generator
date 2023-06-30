package net.ncplanner.plannerator.ncpf;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import java.util.HashMap;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.element.NCPFBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFBlockTagElement;
import net.ncplanner.plannerator.ncpf.element.NCPFFluidElement;
import net.ncplanner.plannerator.ncpf.element.NCPFFluidTagElement;
import net.ncplanner.plannerator.ncpf.element.NCPFItemElement;
import net.ncplanner.plannerator.ncpf.element.NCPFItemTagElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyFluidElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyItemElement;
import net.ncplanner.plannerator.ncpf.element.NCPFOredictElement;
import net.ncplanner.plannerator.ncpf.element.UnknownNCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFElement extends DefinedNCPFModularObject{
    public static HashMap<String, Supplier<NCPFElementDefinition>> recognizedElements = new HashMap<>();
    public static void initRecognizedElements(){
        recognizedElements.clear();
        recognizedElements.put("legacy_block", NCPFLegacyBlockElement::new);
        recognizedElements.put("legacy_item", NCPFLegacyItemElement::new);
        recognizedElements.put("legacy_fluid", NCPFLegacyFluidElement::new);
        recognizedElements.put("oredict", NCPFOredictElement::new);
        recognizedElements.put("block", NCPFBlockElement::new);
        recognizedElements.put("item", NCPFItemElement::new);
        recognizedElements.put("fluid", NCPFFluidElement::new);
        recognizedElements.put("block_tag", NCPFBlockTagElement::new);
        recognizedElements.put("item_tag", NCPFItemTagElement::new);
        recognizedElements.put("fluid_tag", NCPFFluidTagElement::new);
    }
    public NCPFElementDefinition definition;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        definition = recognizedElements.getOrDefault(ncpf.getString("type"), UnknownNCPFElement::new).get();
        definition.convertFromObject(ncpf);
        super.convertFromObject(ncpf);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        definition.convertToObject(ncpf);
        super.convertToObject(ncpf);
    }
}
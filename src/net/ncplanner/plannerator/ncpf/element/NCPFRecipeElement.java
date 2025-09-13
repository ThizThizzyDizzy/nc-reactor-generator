package net.ncplanner.plannerator.ncpf.element;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import net.ncplanner.plannerator.ncpf.NCPFElementStack;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = CoreModule.class)
public class NCPFRecipeElement extends NCPFSettingsElement{
    public HashSet<NCPFElementStack> inputs = new HashSet<>();
    public HashSet<NCPFElementStack> outputs = new HashSet<>();
    public NCPFRecipeElement(){
        super("recipe");
        addElementStacks("inputs", () -> inputs, (elems) -> inputs = elems, "Inputs");
        addElementStacks("outputs", () -> outputs, (elems) -> outputs = elems, "Outputs");
    }
    @Override
    public String getName(){
        ArrayList<String> inputStrs = new ArrayList<>();
        ArrayList<String> outputStrs = new ArrayList<>();
        for(NCPFElementStack stack : inputs)inputStrs.add(stack.toString());
        for(NCPFElementStack stack : outputs)outputStrs.add(stack.toString());
        Collections.sort(inputStrs);
        Collections.sort(outputStrs);
        return "["+String.join(", ", inputStrs)+"]->["+String.join(", ", outputStrs)+"]";
    }
    @Override
    public String getTypeName(){
        return "Recipe";
    }
    public float getOutputRatio(){
        int input = 0;
        for(NCPFElementStack in : inputs)input += in.amount;
        int output = 0;
        for(NCPFElementStack out : outputs)output += out.amount;
        return (float)output/input;
    }
}

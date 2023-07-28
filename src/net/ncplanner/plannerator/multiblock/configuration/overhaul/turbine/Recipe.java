package net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine;
import java.util.ArrayList;
import java.util.Objects;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.multiblock.configuration.ThingWithLegacyNames;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.file.writer.LegacyNCPFWriter;
public class Recipe implements Pinnable, ThingWithLegacyNames{
    public String inputName;
    public String inputDisplayName;
    public ArrayList<String> inputLegacyNames = new ArrayList<>();
    public Image inputTexture;
    public Image inputDisplayTexture;
    public String outputName;
    public String outputDisplayName;
    public Image outputTexture;
    public Image outputDisplayTexture;
    public double power;
    public double coefficient;
    public Recipe(String inputName, String outputName, double power, double coefficient){
        this.inputName = inputName;
        this.outputName = outputName;
        this.power = power;
        this.coefficient = coefficient;
    }
    public Image getInputTexture(){
        return inputTexture;
    }
    public void setInputTexture(Image image){
        inputTexture = image;
        inputDisplayTexture = TextureManager.convert(image);
    }
    public Image getOutputTexture(){
        return outputTexture;
    }
    public void setOutputTexture(Image image){
        outputTexture = image;
        outputDisplayTexture = TextureManager.convert(image);
    }
    @Override
    public boolean equals(Object obj){
        if(obj!=null&&obj instanceof Recipe){
            Recipe r = (Recipe)obj;
            return Objects.equals(inputName, r.inputName)
                    &&Objects.equals(inputDisplayName, r.inputDisplayName)
                    &&inputLegacyNames.equals(r.inputLegacyNames)
                    &&Core.areImagesEqual(inputTexture, r.inputTexture)
                    &&Objects.equals(outputName, r.outputName)
                    &&Objects.equals(outputDisplayName, r.outputDisplayName)
                    &&Core.areImagesEqual(outputTexture, r.outputTexture)
                    &&power==r.power
                    &&coefficient==r.coefficient;
        }
        return false;
    }
    @Override
    public ArrayList<String> getLegacyNames(){
        ArrayList<String> allNames = new ArrayList<>(inputLegacyNames);
        allNames.add(inputName);
        return allNames;
    }
    public String getInputDisplayName(){
        return inputDisplayName==null?inputName:inputDisplayName;
    }
    public String getOutputDisplayName(){
        return outputDisplayName==null?outputName:outputDisplayName;
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        return getSimpleSearchableNames();
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames(){
        ArrayList<String> lst = getLegacyNames();
        lst.add(getInputDisplayName());
        return lst;
    }
    @Override
    public String getPinnedName(){
        return inputName;
    }
    public void setInputName(String inputName){
        this.inputName = inputName;
    }
    public void setInputDisplayName(String inputDisplayName){
        this.inputDisplayName = inputDisplayName;
    }
    public void setInputLegacyNames(ArrayList<String> inputLegacyNames){
        this.inputLegacyNames = new ArrayList<>(inputLegacyNames);
    }
    public void setOutputName(String outputName){
        this.outputName = outputName;
    }
    public void setOutputDisplayName(String outputDisplayName){
        this.outputDisplayName = outputDisplayName;
    }
    public double getPower(){
        return power;
    }
    public double getCoefficient(){
        return coefficient;
    }
    public void setPower(double power){
        this.power = power;
    }
    public void setCoefficient(double coefficient){
        this.coefficient = coefficient;
    }
    @Override
    public String toString(){
        return getInputDisplayName();
    }
}
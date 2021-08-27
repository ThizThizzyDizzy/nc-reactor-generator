package multiblock.configuration.overhaul.fusion;
import java.util.ArrayList;
import java.util.Objects;
import multiblock.configuration.TextureManager;
import planner.Core;
import planner.Pinnable;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
import simplelibrary.image.Image;
public class Recipe implements Pinnable{
    public String inputName;
    public String inputDisplayName;
    public ArrayList<String> inputLegacyNames = new ArrayList<>();
    public Image inputTexture;
    public Image inputDisplayTexture;
    public String outputName;
    public String outputDisplayName;
    public Image outputTexture;
    public Image outputDisplayTexture;
    public float efficiency;
    public int heat;
    public int time;
    public float fluxiness;
    public Recipe(String inputName, String outputName, float efficiency, int heat, int time, float fluxiness){
        this.inputName = inputName;
        this.outputName = outputName;
        this.efficiency = efficiency;
        this.heat = heat;
        this.time = time;
        this.fluxiness = fluxiness;
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
    public Config save(boolean partial){
        Config config = Config.newConfig();
        Config inputCfg = Config.newConfig();
        inputCfg.set("name", inputName);
        if(!partial){
            if(inputDisplayName!=null)inputCfg.set("displayName", inputDisplayName);
            if(!inputLegacyNames.isEmpty()){
                ConfigList lst = new ConfigList();
                for(String s : inputLegacyNames)lst.add(s);
                inputCfg.set("legacyNames", lst);
            }
            if(inputTexture!=null){
                ConfigNumberList tex = new ConfigNumberList();
                tex.add(inputTexture.getWidth());
                for(int x = 0; x<inputTexture.getWidth(); x++){
                    for(int y = 0; y<inputTexture.getHeight(); y++){
                        tex.add(inputTexture.getRGB(x, y));
                    }
                }
                inputCfg.set("texture", tex);
            }
        }
        config.set("input", inputCfg);
        Config outputCfg = Config.newConfig();
        outputCfg.set("name", outputName);
        if(!partial){
            if(outputDisplayName!=null)outputCfg.set("displayName", outputDisplayName);
            if(outputTexture!=null){
                ConfigNumberList tex = new ConfigNumberList();
                tex.add(outputTexture.getWidth());
                for(int x = 0; x<outputTexture.getWidth(); x++){
                    for(int y = 0; y<outputTexture.getHeight(); y++){
                        tex.add(outputTexture.getRGB(x, y));
                    }
                }
                outputCfg.set("texture", tex);
            }
        }
        config.set("output", outputCfg);
        config.set("efficiency", efficiency);
        config.set("heat", heat);
        config.set("time", time);
        config.set("fluxiness", fluxiness);
        return config;
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
                    &&efficiency==r.efficiency
                    &&heat==r.heat
                    &&time==r.time
                    &&fluxiness==r.fluxiness;
        }
        return false;
    }
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
    public float getEfficiency(){
        return efficiency;
    }
    public int getHeat(){
        return heat;
    }
    public int getTime(){
        return time;
    }
    public float getFluxiness(){
        return fluxiness;
    }
    public void setEfficiency(float efficiency){
        this.efficiency = efficiency;
    }
    public void setHeat(int heat){
        this.heat = heat;
    }
    public void setTime(int time){
        this.time = time;
    }
    public void setFluxiness(float fluxiness){
        this.fluxiness = fluxiness;
    }
}
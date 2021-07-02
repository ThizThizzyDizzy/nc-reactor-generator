package multiblock.configuration.overhaul.fusion;
import java.util.ArrayList;
import java.util.Objects;
import multiblock.configuration.TextureManager;
import planner.Core;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
import simplelibrary.image.Image;
public class BlockRecipe{
    public String inputName;
    public String inputDisplayName;
    public ArrayList<String> inputLegacyNames = new ArrayList<>();
    public Image inputTexture;
    public Image inputDisplayTexture;
    public int inputRate;
    public String outputName;
    public String outputDisplayName;
    public Image outputTexture;
    public Image outputDisplayTexture;
    public int outputRate;
    public boolean breedingBlanketAugmented;
    public float breedingBlanketEfficiency;
    public float breedingBlanketHeat;
    public float shieldingShieldiness;
    public float reflectorEfficiency;
    public int heatsinkCooling;
    public BlockRecipe(String inputName, String outputName){
        this.inputName = inputName;
        this.outputName = outputName;
    }
    public Config save(Block block, boolean partial){
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
        if(inputRate!=0)inputCfg.set("rate", inputRate);
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
        if(outputRate!=0)outputCfg.set("rate", outputRate);
        config.set("output", outputCfg);
        if(block.breedingBlanket){
            Config breedingBlanketCfg = Config.newConfig();
            if(breedingBlanketAugmented)breedingBlanketCfg.set("augmented", breedingBlanketAugmented);
            breedingBlanketCfg.set("efficiency", breedingBlanketEfficiency);
            breedingBlanketCfg.set("heat", breedingBlanketHeat);
            config.set("breedingBlanket", breedingBlanketCfg);
        }
        if(block.shielding){
            Config shieldingCfg = Config.newConfig();
            shieldingCfg.set("shieldiness", shieldingShieldiness);
            config.set("shielding", shieldingCfg);
        }
        if(block.reflector){
            Config reflectorCfg = Config.newConfig();
            reflectorCfg.set("efficiency", reflectorEfficiency);
            config.set("reflector", reflectorCfg);
        }
        if(block.heatsink){
            Config heatsinkCfg = Config.newConfig();
            heatsinkCfg.set("cooling", heatsinkCooling);
            config.set("heatsink", heatsinkCfg);
        }
        return config;
    }
    public void setInputTexture(Image image){
        inputTexture = image;
        inputDisplayTexture = TextureManager.convert(image);
    }
    public void setOutputTexture(Image image){
        outputTexture = image;
        outputDisplayTexture = TextureManager.convert(image);
    }
    @Override
    public boolean equals(Object obj){
        if(obj==null)return false;
        BlockRecipe r = (BlockRecipe)obj;
        return Objects.equals(r.inputName, inputName)
                &&Objects.equals(r.inputDisplayName, inputDisplayName)
                &&r.inputLegacyNames.equals(inputLegacyNames)
                &&Core.areImagesEqual(r.inputTexture, inputTexture)
                &&r.inputRate==inputRate
                &&Objects.equals(r.outputName, outputName)
                &&Objects.equals(r.outputDisplayName, outputDisplayName)
                &&Core.areImagesEqual(r.outputTexture, outputTexture)
                &&r.outputRate==outputRate
                &&r.breedingBlanketAugmented==breedingBlanketAugmented
                &&r.breedingBlanketEfficiency==breedingBlanketEfficiency
                &&r.breedingBlanketHeat==breedingBlanketHeat
                &&r.shieldingShieldiness==shieldingShieldiness
                &&r.reflectorEfficiency==reflectorEfficiency
                &&r.heatsinkCooling==heatsinkCooling;
    }
    public String getInputDisplayName(){
        return inputDisplayName==null?inputName:inputDisplayName;
    }
    public ArrayList<String> getLegacyNames(){
        ArrayList<String> allNames = new ArrayList<>(inputLegacyNames);
        allNames.add(inputName);
        return allNames;
    }
}
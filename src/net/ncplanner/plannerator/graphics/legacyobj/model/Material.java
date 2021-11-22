package net.ncplanner.plannerator.graphics.legacyobj.model;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.planner.Core;
public class Material{
    public final String name;
    public String image = "";
    private int texture = 0;
    public float dissolve;
    public float specularExponent;
    public Color diffuseColor;
    public Color specularColor;
    public Color ambientColor;
    public int illuminationModel;
    public Color emmissiveColor;
    public float indexOfRefraction;
    public Material(String name){
        this.name = name;
    }
    public int getTexture(){
        if(texture==0){
            if(image.isEmpty()){
                return 0;
            }
            return Core.getTexture(TextureManager.getImageRaw(image));
        }
        return texture;
    }
}
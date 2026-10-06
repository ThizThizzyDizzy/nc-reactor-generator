package net.ncplanner.plannerator.graphics.model;
import com.thizthizzydizzy.dizzyengine.graphics.image.Color;
import java.util.ArrayList;
@Deprecated //not stable yet!
public class Material{
    public Color diffuseColor = Color.MAGENTA;
    public Color specularColor;
    public ArrayList<Integer> diffuseTextures = new ArrayList<>();
    public ArrayList<Integer> specularTextures = new ArrayList<>();
}
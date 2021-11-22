package net.ncplanner.plannerator.graphics.model;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.image.Color;
@Deprecated //not stable yet!
public class Material{
    public Color diffuseColor = Color.MAGENTA;
    public Color specularColor;
    public ArrayList<Integer> diffuseTextures = new ArrayList<>();
    public ArrayList<Integer> specularTextures = new ArrayList<>();
}
package net.ncplanner.plannerator.graphics.legacyobj.model;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.image.Color;
public class Face {
    public final ArrayList<Integer> verticies;
    public final ArrayList<Integer> textureCoords;
    public final ArrayList<Integer> normals;
    public final Material material;
    public Color colorOverride = null;
    public Face(ArrayList<Integer> verticies, ArrayList<Integer> textureCoords, ArrayList<Integer> normals, Material material){
        this.verticies = new ArrayList<>(verticies);
        this.textureCoords = new ArrayList<>(textureCoords);
        this.normals = new ArrayList<>(normals);
        this.material = material;
    }
    public Face(Face f){
        this(f.verticies, f.textureCoords, f.normals, f.material);
        colorOverride = f.colorOverride;
    }
    public int getTexture(){
        if(colorOverride!=null){
            return 0;
        }
        if(material.getTexture()==0){
            colorOverride = new Color(material.diffuseColor.getRed()/255f, material.diffuseColor.getGreen()/255f, material.diffuseColor.getBlue()/255f, material.dissolve);
            return 0;
        }
        return material.getTexture();
    }
}
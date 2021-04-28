package discord.play.model;
import planner.core.Color;
import java.util.ArrayList;
public class Face{
    public final ArrayList<Integer> verticies;
    public final ArrayList<Integer> textureCoords;
    public final ArrayList<Integer> normals;
    public final Material material;
    public Color colorOverride = null;
    public Face(ArrayList<Integer> verticies, ArrayList<Integer> textureCoords, ArrayList<Integer> normals, Material material){
        this.verticies = verticies;
        this.textureCoords = textureCoords;
        this.normals = normals;
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
            colorOverride = material.getColor();
            return 0;
        }
        return material.getTexture();
    }
}
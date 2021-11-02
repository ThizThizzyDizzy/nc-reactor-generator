package net.ncplanner.plannerator.discord.play.model;
import java.util.ArrayList;
import java.util.Random;
import simplelibrary.image.Color;
public class Model{
    public ArrayList<Face> faces = new ArrayList<>();
    public ArrayList<Line> lines = new ArrayList<>();
    public ArrayList<Vector3f> vertices = new ArrayList<>();
    public ArrayList<Vector3f> normals = new ArrayList<>();
    public ArrayList<float[]> textures = new ArrayList<>();
    public ArrayList<Material> materials = new ArrayList<>();
    public Vector3f origin = new Vector3f(0, 0, 0);
    public double depth, width, height;
    public Model(){}
    public Model(Model m){
        for(Face f : m.faces){
            faces.add(new Face(f));
        }
        for(Line l : m.lines){
            lines.add(new Line(l));
        }
        for(Vector3f v : m.vertices){
            vertices.add(new Vector3f(v));
        }
        for(Vector3f v : m.normals){
            normals.add(new Vector3f(v));
        }
        for(float[] f : m.textures){
            float[] fl = new float[f.length];
            for(int i = 0; i<f.length; i++){
                fl[i] = f[i];
            }
            textures.add(fl);
        }
        materials.addAll(m.materials);
        origin = new Vector3f(m.origin);
    }
    public Material getMaterial(String name){
        for(Material material : materials){
            if(material.name.equals(name)){
                return material;
            }
        }
        return null;
    }
    public void randomizeColors(){
        for(Face face : faces){
            face.colorOverride = new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat());
        }
    }
    public void calculateDimensions(){
        double minX = 0, minY = 0, minZ = 0, maxX = 0, maxY = 0, maxZ = 0;
        for(Vector3f v : vertices){
            minX = Math.min(v.x, minX);
            minY = Math.min(v.y, minY);
            minZ = Math.min(v.z, minZ);
            maxX = Math.max(v.x, maxX);
            maxY = Math.max(v.y, maxY);
            maxZ = Math.max(v.z, maxZ);
        }
        depth = maxY-minY;
        width = maxX-minX;
        height = maxZ-minZ;
    }
    public double getDepth(){
        return depth;
    }
    public double getWidth(){
        return width;
    }
    public double getHeight(){
        return height;
    }
}
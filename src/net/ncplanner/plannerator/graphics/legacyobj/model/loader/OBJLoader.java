package net.ncplanner.plannerator.graphics.legacyobj.model.loader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.graphics.legacyobj.model.Face;
import net.ncplanner.plannerator.graphics.legacyobj.model.Material;
import net.ncplanner.plannerator.graphics.legacyobj.model.Model;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.StringUtil;
import org.joml.Vector3f;
public class OBJLoader{
    public static Model loadModel(InputStream stream, AdjacentFileProvider adjacentProvider) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        Model m = new Model();
        String line;
        ArrayList<Vector3f> loneVerticies = new ArrayList<>();
        Material currentMaterial = null;
        while((line = reader.readLine()) != null){
            //parse object
            if(line.trim().isEmpty()){
                continue;
            }
            if(line.startsWith("#")){
                continue;
            }
            try{
                if(line.startsWith("mtllib ")){
                    if(line.split(" ").length==1)throw new IllegalArgumentException("Failed to parse OBJ command! Expected 1, found "+(line.split(" ").length-1)+" | "
                         + " Full command: "+line);
                    m.materials.addAll(loadMTL(adjacentProvider.getAdjacentFile(line.split(" ", 2)[1]), adjacentProvider));
                }else if(line.startsWith("usemtl ")){
                    expectArgs("OBJ", line, 1);
                    currentMaterial = m.getMaterial(line.split(" ")[1]);
                }else if(line.startsWith("v ")){
                    expectArgs("OBJ", line, 3);
                    float x = Float.valueOf(line.split(" ")[1]);
                    float y = Float.valueOf(line.split(" ")[2]);
                    float z = Float.valueOf(line.split(" ")[3]);
                    m.verticies.add(new Vector3f(x,y,z));
                    loneVerticies.add(m.verticies.get(m.verticies.size()-1));
                    expectArgs("OBJ", line, 3);
                }else if(line.startsWith("vn ")){
                    float x = Float.valueOf(line.split(" ")[1]);
                    float y = Float.valueOf(line.split(" ")[2]);
                    float z = Float.valueOf(line.split(" ")[3]);
                    m.normals.add(new Vector3f(x,y,z));
                    expectArgs("OBJ", line, 3);
                }else if(line.startsWith("vt ")){
                    String vt1 = line.split(" ")[1];
                    String vt2 = line.split(" ")[2];
                    if(vt1.equals("nan"))vt1 = "0";
                    if(vt2.equals("nan"))vt2 = "0";
                    float u = Float.valueOf(vt1);
                    float v = Float.valueOf(vt2);
                    m.textures.add(new float[]{u,v});
                }else if(line.startsWith("o ")){
                    //TODO objects
                }else if(line.startsWith("s ")){
                    //TODO smoothing groups, smooth shading
                }else if(line.startsWith("f ")){
                    ArrayList<Integer> verticies = new ArrayList<>();
                    ArrayList<Integer> textures = new ArrayList<>();
                    ArrayList<Integer> normals = new ArrayList<>();
                    for(int i = 0; i<line.split(" ").length; i++){
                        if(i==0)continue;
                        String v = line.split(" ")[i];
                        if(v.contains("//")){
                            verticies.add(Integer.parseInt(v.split("//")[0]));
                            normals.add(Integer.parseInt(v.split("//")[1]));
                        }else if(v.contains("/")){
                            verticies.add(Integer.parseInt(v.split("/")[0]));
                            textures.add(Integer.parseInt(v.split("/")[1]));
                            if(v.split("/").length>=3){
                                normals.add(Integer.parseInt(v.split("/")[2]));
                            }
                        }else{
                            verticies.add(Integer.parseInt(v));
                        }
                    }
                    for(Integer i : verticies){
                        loneVerticies.remove(m.verticies.get(i-1));
                    }
                    if(verticies.size()>4){
                        m.hasNGons = true;
                    }
                    m.faces.add(new Face(verticies, textures, normals, currentMaterial));
                }else{
                    throw new IllegalArgumentException("Unknown OBJ command: "+line);
                }
            }catch(IOException|IllegalArgumentException ex){
                throw new IOException("Error while parsing OBJ command: "+line, ex);
            }
        }
        if(loneVerticies.size()==1){
            m.origin = new Vector3f(loneVerticies.get(0));
            for(Vector3f v : m.verticies){
                v.x-=m.origin.x;
                v.y-=m.origin.y;
                v.z-=m.origin.z;
            }
        }
        reader.close();
        return m;
    }
    private static void expectArgs(String type, String fullCommand, int expectedArgs){
        if(fullCommand.split(" ").length-1!=expectedArgs){
            throw new IllegalArgumentException("Failed to parse "+type+" command! Expected "+expectedArgs+", found "+(fullCommand.split(" ").length-1)+" | "
                         + " Full command: "+fullCommand);
        }
    }
    private static ArrayList<Material> loadMTL(InputStream stream, AdjacentFileProvider adjacentProvider) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        ArrayList<Material> materials = new ArrayList<>();
        String line;
        Material mat = null;
        while((line = reader.readLine()) != null){
            //parse object
            if(line.trim().isEmpty()){
                continue;
            }
            if(line.startsWith("#")){
                continue;
            }
            try{
                if(line.startsWith("newmtl ")){
                    expectArgs("MTL", line, 1);
                    materials.add(mat = new Material(line.split(" ")[1]));
                }else if(line.startsWith("Ka ")){
                    expectArgs("MTL", line, 3);
                    float r = Float.parseFloat(line.split(" ")[1]);
                    float g = Float.parseFloat(line.split(" ")[2]);
                    float b = Float.parseFloat(line.split(" ")[3]);
                    mat.ambientColor = new Color(r,g,b);
                }else if(line.startsWith("Kd ")){
                    expectArgs("MTL", line, 3);
                    float r = Float.parseFloat(line.split(" ")[1]);
                    float g = Float.parseFloat(line.split(" ")[2]);
                    float b = Float.parseFloat(line.split(" ")[3]);
                    mat.diffuseColor = new Color(r,g,b);
                }else if(line.startsWith("Ke ")){
                    expectArgs("MTL", line, 3);
                    float r = Float.parseFloat(line.split(" ")[1]);
                    float g = Float.parseFloat(line.split(" ")[2]);
                    float b = Float.parseFloat(line.split(" ")[3]);
                    mat.emmissiveColor = new Color(r,g,b);
                }else if(line.startsWith("Ks ")){
                    expectArgs("MTL", line, 3);
                    float r = Float.parseFloat(line.split(" ")[1]);
                    float g = Float.parseFloat(line.split(" ")[2]);
                    float b = Float.parseFloat(line.split(" ")[3]);
                    mat.specularColor = new Color(r,g,b);
                }else if(line.startsWith("Ns ")){
                    expectArgs("MTL", line, 1);
                    mat.specularExponent = Float.parseFloat(line.split(" ")[1]);
                }else if(line.startsWith("d ")){
                    expectArgs("MTL", line, 1);
                    mat.dissolve = Float.parseFloat(line.split(" ")[1]);
                }else if(line.startsWith("illum ")){
                    expectArgs("MTL", line, 1);
                    mat.illuminationModel = Integer.parseInt(line.split(" ")[1]);
                }else if(line.startsWith("Tr ")){
                    expectArgs("MTL", line, 1);
                    mat.dissolve = 1-Float.parseFloat(line.split(" ")[1]);
                }else if(line.startsWith("Ni ")){
                    expectArgs("MTL", line, 1);
                    mat.indexOfRefraction = Float.parseFloat(line.split(" ")[1]);
                }else if(line.startsWith("map_Kd ")){
                    expectArgs("MTL", line, 1);
                    mat.image = adjacentProvider.getAdjacentFilepath(line.split(" ")[1]);
                }else{
                    throw new IllegalArgumentException("Unknown MTL command: "+line);
                }
            }catch(NumberFormatException ex){
                throw new IOException("Error while parsing MTL command: "+line, ex);
            }
        }
        reader.close();
        return materials;
    }
    private static HashMap<String, Model> models = new HashMap<>();
    public static Model getModel(String path){
        if(models.containsKey(path))return models.get(path);
        try{
            Model m = loadModel(Core.getInputStream(path), new AdjacentFileProvider() {
                @Override
                public InputStream getAdjacentFile(String name){
                    return Core.getInputStream(getAdjacentFilepath(name));
                }
                @Override
                public String getAdjacentFilepath(String name){
                    String[] strs = StringUtil.split(StringUtil.replace(path, "\\", "/"), "/");
                    return path.substring(0, path.length()-strs[strs.length-1].length())+name;
                }
            });
            models.put(path, m);
            return m;
        }catch(IOException ex){
            Core.error("Failed to load model: "+path, ex);
            return null;
        }
    }
}
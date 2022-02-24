package net.ncplanner.plannerator.discord.play.model;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.planner.Core;
public class Material{
    public static HashMap<String, ArrayList<Material>> loadedMaterials = new HashMap<>();
    public final String name;
    private String image = "";
    private int texture = 0;
    private double dissolve;
    private Color diffuseColor;
    public Material(String name){
        this.name = name;
    }
    public int getTexture(){
        if(texture==0){
            if(image.isEmpty()){
                return 0;
            }
            return Core.getTexture(TextureManager.getImageRaw(name));
        }
        return texture;
    }
    public static ArrayList<Material> loadMTL(String modelpath, final String filename) throws FileNotFoundException, IOException{
        File file = new File(modelpath);
        String modelname = file.getName();
        final String root = modelpath.replace(modelname, "");
        if(loadedMaterials.containsKey(root+filename)){
            return loadedMaterials.get(root+filename);
        }
        BufferedReader reader;
        ArrayList<Material> materials = new ArrayList<>();
        if(modelpath.startsWith("/")){
            InputStream stream = Core.getInputStream(root+filename);
            if(stream==null){
                Core.error("Can't find material: "+root+filename, null);
                loadedMaterials.put(root+filename, null);
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(stream));
        }else{
            reader = new BufferedReader(new FileReader(new File(root+filename)));
        }
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
                    expectArgs(line, 1);
                    materials.add(mat = new Material(line.split(" ")[1]));
                }else if(line.startsWith("Kd ")){
                    expectArgs(line, 3);
                    float r = Float.parseFloat(line.split(" ")[1]);
                    float g = Float.parseFloat(line.split(" ")[2]);
                    float b = Float.parseFloat(line.split(" ")[3]);
                    mat.diffuseColor = new Color(r,g,b);
                }else if(line.startsWith("d ")){
                    expectArgs(line, 1);
                    mat.dissolve = Float.parseFloat(line.split(" ")[1]);
                }else if(line.startsWith("map_Kd ")){
                    expectArgs(line, 1);
                    mat.image = root+line.split(" ")[1];
                }else{
                    System.err.println("Unknown MTL command: "+line);
                }
            }catch(Exception ex){
                Core.error("Error while parsing MTL command: "+line, ex);
            }
        }
        reader.close();
        loadedMaterials.put(root+filename, materials);
        return materials;
    }
    private static void expectArgs(String fullCommand, int expectedArgs){
        if(fullCommand.split(" ").length-1!=expectedArgs){
            System.err.println("Failed to parse MTL command! Expected "+expectedArgs+", found "+(fullCommand.split(" ").length-1)+"\n"
                         + " Full command: "+fullCommand+"");
        }
    }
    public Color getColor(){
        return diffuseColor;
    }
}
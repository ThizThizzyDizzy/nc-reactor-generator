package discord.play.model;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.texture.TexturePackManager;
public class OBJLoader{
    public static HashMap<String, Model> loadedModels = new HashMap<>();
    public static Model loadModel(final String filepath) throws FileNotFoundException, IOException{
        if(loadedModels.containsKey(filepath)){
            if(loadedModels.get(filepath)==null)return null;
            return new Model(loadedModels.get(filepath));
        }
        BufferedReader reader;
        if(filepath.startsWith("/")){
            InputStream stream = TexturePackManager.instance.currentTexturePack.getResourceAsStream(filepath);
            if(stream==null){
                Sys.error(ErrorLevel.moderate, "Can't find model: "+filepath, null, ErrorCategory.fileIO);
                loadedModels.put(filepath, null);
                return new Model();
            }
            reader = new BufferedReader(new InputStreamReader(stream));
        }else{
            reader = new BufferedReader(new FileReader(new File(filepath)));
        }
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
                    expectArgs(line,1);
                    m.materials.addAll(Material.loadMTL(filepath, line.split(" ")[1]));
                }else if(line.startsWith("usemtl ")){
                    expectArgs(line, 1);
                    currentMaterial = m.getMaterial(line.split(" ")[1]);
                }else if(line.startsWith("v ")){
                    expectArgs(line, 3);
                    float x = Float.valueOf(line.split(" ")[1]);
                    float y = Float.valueOf(line.split(" ")[2]);
                    float z = Float.valueOf(line.split(" ")[3]);
                    m.vertices.add(new Vector3f(x,y,z));
                    loneVerticies.add(m.vertices.get(m.vertices.size()-1));
                    expectArgs(line, 3);
                }else if(line.startsWith("vn ")){
                    float x = Float.valueOf(line.split(" ")[1]);
                    float y = Float.valueOf(line.split(" ")[2]);
                    float z = Float.valueOf(line.split(" ")[3]);
                    m.normals.add(new Vector3f(x,y,z));
                    expectArgs(line, 3);
                }else if(line.startsWith("vt ")){
                    float u = Float.valueOf(line.split(" ")[1]);
                    float v = Float.valueOf(line.split(" ")[2]);
                    m.textures.add(new float[]{u,v});
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
                        loneVerticies.remove(m.vertices.get(i-1));
                    }
                    m.faces.add(new Face(verticies, textures, normals, currentMaterial));
                }else{
                    System.err.println("Unknown OBJ command: "+line);
                }
            }catch(Exception ex){
                Sys.error(ErrorLevel.minor, "Error while parsing OBJ command: "+line, ex, ErrorCategory.fileIO);
            }
        }
        if(loneVerticies.size()==1){
            m.origin = new Vector3f(loneVerticies.get(0));
            for(Vector3f v : m.vertices){
                v.x-=m.origin.x;
                v.y-=m.origin.y;
                v.z-=m.origin.z;
            }
        }
        reader.close();
        loadedModels.put(filepath, m);
        return m;
    }
    private static void expectArgs(String fullCommand, int expectedArgs){
        if(fullCommand.split(" ").length-1!=expectedArgs){
            System.err.println("Failed to parse OBJ command! Expected "+expectedArgs+", found "+(fullCommand.split(" ").length-1)+"\n"
                         + " Full command: "+fullCommand+"");
        }
    }
}
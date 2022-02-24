package net.ncplanner.plannerator.graphics.model;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Shader;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
@Deprecated //not stable yet!
public class Mesh{
    public ArrayList<Vertex> verticies = new ArrayList<>();
    public ArrayList<Integer> indices = new ArrayList<>();
    public ArrayList<Material> materials = new ArrayList<>();
    private int vao, vbo, ebo;
    @Deprecated //not stable yet!
    public Mesh(ArrayList<Vertex> verticies, ArrayList<Integer> indicies, ArrayList<Material> textures){
        this.verticies = verticies;
        this.indices = indicies;
        this.materials = textures;
        setupMesh();
    }
    public void draw(Shader shader){
        int i = 0;
        int di = 0, si = 0;
        for(int m = 0; m<materials.size(); m++){
            Material mat = materials.get(m);
            for(int id : mat.diffuseTextures){
                glActiveTexture(GL_TEXTURE0+i);
                glBindTexture(GL_TEXTURE_2D, id);
                i++;
                shader.setUniform1i("diffTex"+ ++di, id);
            }
            for(int id : mat.specularTextures){
                glActiveTexture(GL_TEXTURE0+i);
                glBindTexture(GL_TEXTURE_2D, id);
                i++;
                shader.setUniform1i("specTex"+ ++si, id);
            }
            if(mat.diffuseColor!=null)shader.setUniform4f("diffColor"+m, mat.diffuseColor.getRed()/255f, mat.diffuseColor.getGreen()/255f, mat.diffuseColor.getBlue()/255f, mat.diffuseColor.getAlpha()/255f);
            if(mat.specularColor!=null)shader.setUniform4f("specColor"+m, mat.specularColor.getRed()/255f, mat.specularColor.getGreen()/255f, mat.specularColor.getBlue()/255f, mat.specularColor.getAlpha()/255f);
        }
        shader.setUniform1i("numDiffTex", di);
        shader.setUniform1i("numSpecTex", si);
        glActiveTexture(GL_TEXTURE0);
        
        //draw mesh
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, indices.size(), GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }
    private void setupMesh(){
        //make the buffers
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();
        
        //bind vao
        glBindVertexArray(vao);
        
        //add verticies
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, getVertsF(), GL_STATIC_DRAW);
        
        //add indicies
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, getIndiciesI(), GL_STATIC_DRAW);
        
        //vertex positions
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8*4, 0);
        
        //vertex normals
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8*4, 3*4);
        
        //vertex texture coords
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8*4, 6*4);
        
        //unbind vao
        glBindVertexArray(0);
    }
    private float[] getVertsF(){
        float[] data = new float[verticies.size()*8];
        for(int i = 0; i<verticies.size(); i++){
            Vertex vert = verticies.get(i);
            data[i*8] = vert.pos.x;
            data[i*8+1] = vert.pos.y;
            data[i*8+2] = vert.pos.z;
            data[i*8+3] = vert.normal.x;
            data[i*8+4] = vert.normal.y;
            data[i*8+5] = vert.normal.z;
            data[i*8+6] = vert.texCoords.x;
            data[i*8+7] = vert.texCoords.y;
        }
        return data;
    }
    private int[] getIndiciesI(){
        int[] data = new int[indices.size()];
        for(int i = 0; i<indices.size(); i++){
            data[i] = indices.get(i);
        }
        return data;
    }
}
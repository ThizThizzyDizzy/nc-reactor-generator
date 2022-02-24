package net.ncplanner.plannerator.graphics.model;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Shader;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.Core;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;
@Deprecated //not stable yet!
public class Model{
    private ArrayList<Mesh> meshes = new ArrayList<>();
    String dir;
    @Deprecated //not stable yet!
    public Model(String path, String type){
        AIScene scene = Assimp.aiImportFileFromMemory(Core.loadData(Core.getInputStream(path)), Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs, type);
        dir = path.substring(0, path.lastIndexOf("/"));
        processNode(scene.mRootNode(), scene);
    }
    public void draw(Shader shader){
        for(Mesh mesh : meshes)mesh.draw(shader);
    }
    private void processNode(AINode node, AIScene scene){
        AIMatrix4x4 transform = node.mTransformation();
        for(int i = 0; i<node.mNumMeshes(); i++){
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(node.mMeshes().get(i)));
            meshes.add(processMesh(mesh, scene));
        }
        for(int i = 0; i<node.mNumChildren(); i++){
            processNode(AINode.create(node.mChildren().get(i)), scene);
        }
    }
    private Mesh processMesh(AIMesh mesh, AIScene scene){
        ArrayList<Vertex> verticies = new ArrayList<>();
        ArrayList<Integer> indicies = new ArrayList<>();
        ArrayList<Material> materials = new ArrayList<>();
        for(int i = 0; i<mesh.mNumVertices(); i++){
            Vertex vertex = new Vertex();
            AIVector3D pos = mesh.mVertices().get(i);
            vertex.pos = new Vector3f(pos.x(), pos.y(), pos.z());
            AIVector3D normal = mesh.mNormals().get(i);
            vertex.normal = new Vector3f(normal.x(), normal.y(), normal.z());
            if(mesh.mTextureCoords(0)!=null){
                AIVector3D texCoords = mesh.mTextureCoords(0).get(i);
                vertex.texCoords = new Vector2f(texCoords.x(), texCoords.y());
            }else vertex.texCoords = new Vector2f();
            verticies.add(vertex);
        }
        for(int i = 0; i<mesh.mNumFaces(); i++){
            AIFace face = mesh.mFaces().get(i);
            for(int j = 0; j<face.mNumIndices(); j++)indicies.add(face.mIndices().get(j));
        }
        if(mesh.mMaterialIndex()>=0){
            AIMaterial material = AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex()));
            materials.add(loadMaterial(material, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_DIFFUSE));
            materials.add(loadMaterial(material, Assimp.AI_MATKEY_COLOR_SPECULAR, Assimp.aiTextureType_SPECULAR));
        }
        return new Mesh(verticies, indicies, materials);
    }
    private Material loadMaterial(AIMaterial mat, String aiColType, int aitype){
        Material material = new Material();
        ArrayList<Integer> textures;
        switch(aitype){
            case Assimp.aiTextureType_DIFFUSE:
                textures = material.diffuseTextures;
                break;
            case Assimp.aiTextureType_SPECULAR:
                textures = material.specularTextures;
                break;
            default:
                throw new IllegalArgumentException("Unknown texture type: "+aitype+"!");
        }
        AIColor4D col = AIColor4D.mallocStack();
        if(Assimp.aiGetMaterialColor(mat, aiColType, 0, 0, col)>0){
            Color color = new Color(col.r(),col.g(),col.b(),col.a());
            switch(aiColType){
                case Assimp.AI_MATKEY_COLOR_DIFFUSE:
                    material.diffuseColor = color;
                    System.out.println(color.getRed()+" "+color.getGreen()+" "+color.getBlue()+" "+color.getAlpha());
                    break;
                case Assimp.AI_MATKEY_COLOR_SPECULAR:
                    material.specularColor = color;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown color type: "+aiColType+"!");
            }
        }
        for(int i = 0; i<Assimp.aiGetMaterialTextureCount(mat, aitype); i++){
            AIString path = AIString.mallocStack();
            Assimp.aiGetMaterialTexture(mat, aitype, i, path, null, null, new float[1], null, null, null);
            textures.add(Core.loadTexture(dir+"/"+path));
        }
        return material;
    }
}
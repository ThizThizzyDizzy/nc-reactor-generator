package discord.play.smivilization;
import discord.play.smivilization.thing.*;
import discord.Bot;
import discord.play.model.Face;
import discord.play.model.Model;
import discord.play.model.OBJLoader;
import discord.play.smivilization.thing.special.*;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import planner.Core;
import simplelibrary.CircularStream;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public class Hut{
    public static long PRICE = 32;
    public final long owner;
    public static final ArrayList<HutThing> allFurniture = new ArrayList<>();
    static{
        allFurniture.add(new Lamp(null, null));
        allFurniture.add(new Shelf(null, null));
        allFurniture.add(new Couch(null, null));
        allFurniture.add(new Bed(null, null));
        allFurniture.add(new PurpleTriflorice(null, null));
        allFurniture.add(new PuLaptop(null, null));
        allFurniture.add(new Table(null, null));
        allFurniture.add(new Television(null, null));
        allFurniture.add(new CoffeeTable(null, null));
        allFurniture.add(new TomPainting(null, null));
        allFurniture.add(new GlowshroomGlowshroomGlowshroomPoster(null, null));
        allFurniture.add(new SmoreRug(null, null));
    }
    public ArrayList<Long> invited = new ArrayList<>();
    public ArrayList<HutThing> furniture = new ArrayList<>();
    public boolean open;
    public Hut(long owner){
        this.owner = owner;
        if(owner==210340018198151170l){
            furniture.add(new GlowshroomGlowshroomGlowshroomPoster(null, null));
        }
    }
    public Config save(Config config){
        config.set("owner", owner);
        ConfigNumberList inviteds = new ConfigNumberList();
        for(Long l : invited){
            inviteds.add(l);
        }
        config.set("invited", inviteds);
        ConfigList furn = new ConfigList();
        for(HutThing thing : furniture){
            furn.add(thing.save(Config.newConfig()));
        }
        config.set("furniture", furn);
        config.set("open", open);
        return config;
    }
    public static Hut load(Config config){
        Hut hut = new Hut(config.get("owner"));
        ConfigNumberList inviteds = config.get("invited");
        for(int i = 0; i<inviteds.size(); i++){
            hut.invited.add(inviteds.get(i));
        }
        ConfigList furn = config.get("furniture", new ConfigList());
        for(Object c : furn.iterable()){
            HutThing thing = HutThing.load((Config)c, hut);
            if(thing instanceof HutThingExclusive){
                if(hut.owner!=((HutThingExclusive)thing).exclusiveOwner)continue;
            }
            hut.furniture.add(thing);
        }
        hut.open = config.get("open", false);
        return hut;
    }
    public boolean isAllowedInside(User user){
        if(open)return true;
        return invited.contains(user.getIdLong());
    }
    public void sendImage(MessageChannel channel, String name, Core.BufferRenderer renderer){
        sendImage(channel, name, 512, 512, renderer);
    }
    public void sendImage(MessageChannel channel, String name, int width, int height, Core.BufferRenderer renderer){
        CircularStream stream = new CircularStream(1024*1024);//1MB
        CompletableFuture<Message> submit = channel.sendFile(stream.getInput(), name+".png").submit();
        try{
            ImageIO.write(Bot.makeImage(width, height, renderer), "png", stream);
            stream.close();
        }catch(Exception ex){
            Bot.printErrorMessage(channel, "Failed to write file", ex);
            submit.cancel(true);
            stream.close();
        }
    }
    public void sendExteriorImage(MessageChannel channel){
        sendImage(channel, "outside", (buff) -> {
            Renderer2D.drawRect(0, 0, buff.width, buff.height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/outside.png"));
        });
    }
    public void sendInteriorImage(MessageChannel channel){
        sendImage(channel, "inside", (buff) -> {
            Renderer2D.drawRect(0, 0, buff.width, buff.height, ImageStash.instance.getTexture("/textures/smivilization/buildings/huts/gliese/inside.png"));
            ArrayList<HutThing> furn = new ArrayList<>(furniture);
            for(HutThing thing : furn){
                GL11.glColor4d(1, 1, 1, 1);
                thing.render(buff.width, buff.height);
            }
//            for(PlacementPoint point : getPlacementPoints()){
//                Core.applyColor(Color.white);
//                Renderer2D.drawRect(point.vx-2*point.scale, point.vy-2*point.scale, point.vx+2*point.scale, point.vy+2*point.scale, 0);
//            }
//            try{
//                drawa3DModelLikeTotalMagic(8, 0, 5, OBJLoader.loadModel("C:/Users/Thiz/Desktop/untitled.obj"));
//                drawa3DModelLikeTotalMagic(2, 0, 5, OBJLoader.loadModel("C:/Users/Thiz/Desktop/untitled.obj"));
//                drawa3DModelLikeTotalMagic(8, 10, 5, OBJLoader.loadModel("C:/Users/Thiz/Desktop/untitled.obj"));
//                drawa3DModelLikeTotalMagic(2, 10, 5, OBJLoader.loadModel("C:/Users/Thiz/Desktop/untitled.obj"));
//            }catch(IOException ex){}
        });
    }
    public ArrayList<PlacementPoint> getPlacementPoints(){
        ArrayList<PlacementPoint> points = new ArrayList<>();
        addHorizontalPlacementPointGrid2048(0, 0, 0, 10, 10, points, 543, 1336, 1581, 1320, 189, 1978, 1876, 1969);//floor
        addHorizontalPlacementPointGrid2048(0, 0, 10, 10, 10, points, 567, 258, 1628, 227, 16, 16, 2032, 16);//cieling
        addBackWallPlacementPointGrid2048(10, 10, 10, 3, 2, 6, 6, points, 600, 340, 1600, 310, 570, 1240, 1580, 1220);//back wall, excluding window
        addHorizontalPlacementPointGrid2048(3, 10, 4, 4, 1, points, 931, 886, 1258, 887, 931, 886, 1258, 887);//window sill
        addSideWallPlacementPointGrid2048(10, 10, 0, points, 520, 335, 65, 222, 478, 1273, 42, 1912);//left wall
        addSideWallPlacementPointGrid2048(10, 10, 0, points, 1683, 305, 2003, 200, 1672, 1282, 2002, 1845);//right wall
        return points;
    }
    private void addHorizontalPlacementPointGrid2048(int xOff, int yOff, int z, int width, int depth, List<PlacementPoint> points, int backLeftX, int backLeftY, int backRightX, int backRightY, int frontLeftX, int frontLeftY, int frontRightX, int frontRightY){
        addHorizontalPlacementPointGrid512(xOff, yOff, z, width, depth, points, backLeftX/4, backLeftY/4, backRightX/4, backRightY/4, frontLeftX/4, frontLeftY/4, frontRightX/4, frontRightY/4);
    }
    private void addHorizontalPlacementPointGrid512(int xOff, int yOff, int z, int width, int depth, List<PlacementPoint> points, int backLeftX, int backLeftY, int backRightX, int backRightY, int frontLeftX, int frontLeftY, int frontRightX, int frontRightY){
        int backSize = backRightX-backLeftX;
        int frontSize = frontRightX-frontLeftX;
        int widthDiff = frontSize-backSize;
        int leftSize = frontLeftY-backLeftY;
        int rightSize = frontRightY-backRightY;
        int depthDiff = rightSize-leftSize;
        for(int y = 0; y<depth; y++){
            float Y = doSomeMagic(y/(float)depth)*depth;
            float dep = (Y/(float)(depth-1));
            if(Float.isNaN(dep))dep = 0;
            int wide = (int)(backSize+widthDiff*dep);
            float scale = wide/(float)backSize;
            int left = (int)(backLeftX+(frontLeftX-backLeftX)*dep);
            for(int x = 0; x<width; x++){
                float wid = (x/(float)(width-1));
                if(Float.isNaN(wid))wid = 0;
                int deep = (int)(leftSize+depthDiff*wid);
                int back = (int)(backLeftY+(backRightY-backLeftY)*wid);
                float w = wide/((float)width-1);
                if(Float.isNaN(w))w = 0;
                float d = deep/((float)depth-1);
                if(Float.isNaN(d))d = 0;
                points.add(new PlacementPoint(this, x+xOff, y+yOff, z, (int)(left+x*w), (int)(back+Y*d), scale));
            }
        }
    }
    private void addBackWallPlacementPointGrid2048(int width, int height, int y, List<PlacementPoint> points, int backLeftX, int backLeftY, int backRightX, int backRightY, int frontLeftX, int frontLeftY, int frontRightX, int frontRightY){
        addBackWallPlacementPointGrid2048(width, height, y, -1, -1, -1, -1, points, backLeftX, backLeftY, backRightX, backRightY, frontLeftX, frontLeftY, frontRightX, frontRightY);
    }
    private void addBackWallPlacementPointGrid512(int width, int height, int y, List<PlacementPoint> points, int topLeftX, int topLeftY, int topRightX, int topRightY, int bottomLeftX, int bottomLeftY, int bottomRightX, int bottomRightY){
        addBackWallPlacementPointGrid512(width, height, y, -1, -1, -1, -1, points, topLeftX, topLeftY, topRightX, topRightY, bottomLeftX, bottomLeftY, bottomRightX, bottomRightY);
    }
    private void addBackWallPlacementPointGrid2048(int width, int height, int y, int excludeX1, int excludeZ1, int excludeX2, int excludeZ2, List<PlacementPoint> points, int topLeftX, int topLeftY, int topRightX, int topRightY, int bottomLeftX, int bottomLeftY, int bottomRightX, int bottomRightY){
        addBackWallPlacementPointGrid512(width, height, y, excludeX1, excludeZ1, excludeX2, excludeZ2, points, topLeftX/4, topLeftY/4, topRightX/4, topRightY/4, bottomLeftX/4, bottomLeftY/4, bottomRightX/4, bottomRightY/4);
    }
    private void addBackWallPlacementPointGrid512(int width, int height, int y, int excludeX1, int excludeZ1, int excludeX2, int excludeZ2, List<PlacementPoint> points, int topLeftX, int topLeftY, int topRightX, int topRightY, int bottomLeftX, int bottomLeftY, int bottomRightX, int bottomRightY){
        int topSize = topRightX-topLeftX;
        int bottomSize = bottomRightX-bottomLeftX;
        int widthDiff = bottomSize-topSize;
        int leftSize = bottomLeftY-topLeftY;
        int rightSize = bottomRightY-topRightY;
        int heightDiff = rightSize-leftSize;
        for(int z = 0; z<height; z++){
            float hi = (z/(float)(height-1));
            int wide = (int)(topSize+widthDiff*hi);
            int left = (int)(topLeftX+(bottomLeftX-topLeftX)*hi);
            for(int x = 0; x<width; x++){
                if(x>=excludeX1&&x<=excludeX2&&z>=excludeZ1&&z<=excludeZ2)continue;
                float wid = (x/(float)(width-1));
                int high = (int)(leftSize+heightDiff*wid);
                int top = (int)(topLeftY+(topRightY-topLeftY)*wid);
                points.add(new PlacementPoint(this, x, y, z, left+x*wide/(width-1), top+z*high/(height-1), 1));
            }
        }
    }
    private void addSideWallPlacementPointGrid2048(int depth, int height, int x, List<PlacementPoint> points, int topBackX, int topBackY, int topFrontX, int topFrontY, int bottomBackX, int bottomBackY, int bottomFrontX, int bottomFrontY){
        addSideWallPlacementPointGrid512(depth, height, x, points, topBackX/4, topBackY/4, topFrontX/4, topFrontY/4, bottomBackX/4, bottomBackY/4, bottomFrontX/4, bottomFrontY/4);
    }
    private void addSideWallPlacementPointGrid512(int depth, int height, int x, List<PlacementPoint> points, int topBackX, int topBackY, int topFrontX, int topFrontY, int bottomBackX, int bottomBackY, int bottomFrontX, int bottomFrontY){
        int topSize = topFrontX-topBackX;
        int bottomSize = bottomFrontX-bottomBackX;
        int depthDiff = bottomSize-topSize;
        int backSize = bottomBackY-topBackY;
        int frontSize = bottomFrontY-topFrontY;
        int heightDiff = frontSize-backSize;
        for(int z = 0; z<height; z++){
            float hi = (z/(float)(height-1));
            int deep = (int)(topSize+depthDiff*hi);
            int back = (int)(topBackX+(bottomBackX-topBackX)*hi);
            for(int y = 0; y<depth; y++){
                float dep = (y/(float)(depth-1));
                int high = (int)(backSize+heightDiff*dep);
                int top = (int)(topBackY+(topFrontY-topBackY)*dep);
                float scale = high/(float)backSize;
                points.add(new PlacementPoint(this, x, y, z, back+y*deep/(depth-1), top+z*high/(height-1), scale));
            }
        }
    }
    private float doSomeMagic(float f){
        return (float)Math.pow(f,1.25f);
    }
    private void drawa3DModelLikeTotalMagic(float x, float y, float z, Model model){
        Collections.sort(model.faces, (o1, o2) -> {
            float y1 = 0;
            float z1 = 0;
            for(int i : o1.verticies){
                Vector3f vertex = model.vertices.get(i-1);
                y1+=vertex.y;
                z1+=vertex.z;
            }
            y1/=o1.verticies.size();
            z1/=o1.verticies.size();
            float y2 = 0;
            float z2 = 0;
            for(int i : o2.verticies){
                Vector3f vertex = model.vertices.get(i-1);
                y2+=vertex.y;
                z2+=vertex.z;
            }
            y2/=o2.verticies.size();
            z2/=o2.verticies.size();
            if(y1==y2)return (int)((z1-z2)*1000);
            return (int)((y1-y2)*1000);
        });
        int oldTexture = -1;
        int oldPolygonSize = 0;
        for (Face face : model.faces) {
//            int faceY = 0;
//            for(int i : face.verticies){
//                Vector3f vertex = model.vertices.get(i-1);
//                faceY+=vertex.y;
//            }
//            faceY/=face.verticies.size();
//            if(faceY<.1f)continue;
            int texture = face.getTexture();
            int polygonSize = face.verticies.size();
            if(oldTexture!=texture||oldPolygonSize!=polygonSize){
                if(oldTexture!=-1||oldPolygonSize!=polygonSize){
                    GL11.glEnd();
                }
                ImageStash.instance.bindTexture(texture);
                switch(polygonSize){
                    case 4:
                        GL11.glBegin(GL11.GL_QUADS);
                        break;
                    default:
                        if(polygonSize<3){
                            throw new IllegalArgumentException("Cannot draw face with "+polygonSize+" vertecies!");
                        }
                    case 3:
                        GL11.glBegin(GL11.GL_TRIANGLES);
                        break;
                }
            }
//            if(face.colorOverride!=null){
//                GL11.glColor4d(face.colorOverride.getRed()/255d, face.colorOverride.getGreen()/255d, face.colorOverride.getBlue()/255d, 1);
//            }
            oldTexture = texture;
            oldPolygonSize = polygonSize;
            if(polygonSize>4){
                for(int i = 0; i < face.verticies.size()-2; i++){
                    int vert1 = face.verticies.get(0);
                    int vert2 = face.verticies.get(i+1);
                    int vert3 = face.verticies.get(i+2);
                    if(face.textureCoords.size()>0){
                        float[] uv = model.textures.get(face.textureCoords.get(0)-1);
                        GL11.glTexCoord2f(uv[0], -uv[1]);
                    }
                    if(face.normals.size()>0){
                        Vector3f n = model.normals.get((int)face.normals.get(0)-1);
                        setNormals(face.colorOverride, n.x, n.y, n.z);
                    }
                    Vector3f v = model.vertices.get(vert1 - 1);
                    vertex(x+v.x, y+v.y, z+v.z);
                    if(face.textureCoords.size()>0){
                        float[] uv = model.textures.get(face.textureCoords.get(i+1)-1);
                        GL11.glTexCoord2f(uv[0], -uv[1]);
                    }
                    if(face.normals.size()>0){
                        Vector3f n = model.normals.get((int)face.normals.get(i+1)-1);
                        setNormals(face.colorOverride, n.x, n.y, n.z);
                    }
                    v = model.vertices.get(vert2 - 1);
                    vertex(x+v.x, y+v.y, z+v.z);
                    if(face.textureCoords.size()>0){
                        float[] uv = model.textures.get(face.textureCoords.get(i+2)-1);
                        GL11.glTexCoord2f(uv[0], -uv[1]);
                    }
                    if(face.normals.size()>0){
                        Vector3f n = model.normals.get((int)face.normals.get(i+2)-1);
                        setNormals(face.colorOverride, n.x, n.y, n.z);
                    }
                    v = model.vertices.get(vert3 - 1);
                    vertex(x+v.x, y+v.y, z+v.z);
                }
            }else{
                for(int i = 0; i < face.verticies.size(); i++){
                    int vert = face.verticies.get(i);
                    if(face.textureCoords.size()>0){
                        float[] uv = model.textures.get(face.textureCoords.get(i)-1);
                        GL11.glTexCoord2f(uv[0], -uv[1]);
                    }
                    if(face.normals.size()>0){
                        Vector3f n = model.normals.get((int)face.normals.get(i)-1);
                        setNormals(face.colorOverride, n.x, n.y, n.z);
                    }
                    Vector3f v = model.vertices.get(vert - 1);
                    vertex(x+v.x, y+v.y, z+v.z);
                }
            }
        }
        GL11.glEnd();
    }
    private void setNormals(Color color, float x, float y, float z){
        if(color==null)color = Color.white;
        x*=1.25;
        z*=.75;
        float mod = 20;
        float total = ((x+y+z)-3)*mod;
        GL11.glColor4f((color.getRed()+total)/255, (color.getGreen()+total)/255, (color.getBlue()+total)/255, 1);
    }
    private void vertex(float x, float y, float z){
        float[] pos = convertXYZtoXY512(x, y, z);
        GL11.glVertex2f(pos[0],pos[1]);
    }
    private float[] convertXYZtoXY512(float x, float y, float z){
        float[] xy = convertXYZtoXY2048(x, y, z);
        return new float[]{xy[0]/4,xy[1]/4};
    }
    private float[] convertXYZtoXY2048(float x, float y, float z){
        x/=10;
        y/=10;
        z/=10;
        //xyz are 0-1
        int[] backTopLeft = {560, 305};
        int[] backTopRight = {1636, 280};
        int[] backBottomLeft = {513, 1295};
        int[] backBottomRight = {1623,1283};
        int[] frontTopLeft = {-221,0};
        int[] frontTopRight = {2296,0};
        int[] frontBottomLeft = {0,2100};
        int[] frontBottomRight = {2048,2048};
        int[] topLeftDiff = {frontTopLeft[0]-backTopLeft[0],frontTopLeft[1]-backTopLeft[1]};
        int[] topRightDiff = {frontTopRight[0]-backTopRight[0],frontTopRight[1]-backTopRight[1]};
        int[] bottomLeftDiff = {frontBottomLeft[0]-backBottomLeft[0],frontBottomLeft[1]-backBottomLeft[1]};
        int[] bottomRightDiff = {frontBottomRight[0]-backBottomRight[0],frontBottomRight[1]-backBottomRight[1]};
        float[] topLeft = {backTopLeft[0]+topLeftDiff[0]*y,backTopLeft[1]+topLeftDiff[1]*y};
        float[] topRight = {backTopRight[0]+topRightDiff[0]*y,backTopRight[1]+topRightDiff[1]*y};
        float[] bottomLeft = {backBottomLeft[0]+bottomLeftDiff[0]*y,backBottomLeft[1]+bottomLeftDiff[1]*y};
        float[] bottomRight = {backBottomRight[0]+bottomRightDiff[0]*y,backBottomRight[1]+bottomRightDiff[1]*y};
        float leftHeight = Math.abs(topLeft[1]-bottomLeft[1]);//left height at depth
        float rightHeight = Math.abs(topRight[1]-bottomRight[1]);//right height at depth
        float bottomWidth = Math.abs(bottomLeft[0]-bottomRight[0]);//bottom width at depth
        float topWidth = Math.abs(topLeft[0]-topRight[0]);//top width at depth
        float bottomX = bottomLeft[0]+bottomWidth*x;
        float topX = topLeft[0]+topWidth*x;
        float leftY = bottomLeft[1]-leftHeight*z;
        float rightY = bottomRight[1]-rightHeight*z;
        float xDiff = topX-bottomX;
        float yDiff = rightY-leftY;
        float X = bottomX+xDiff*x;
        float Y = leftY+yDiff*z;
        return new float[]{X,Y};
    }
}
package discord.play.model;
public class Vector3f{
    public float x,y,z;
    public Vector3f(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector3f(Vector3f vec){
        x = vec.x;
        y = vec.y;
        z = vec.z;
    }
}
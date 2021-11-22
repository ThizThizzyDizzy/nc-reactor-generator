package net.ncplanner.plannerator.graphics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import net.ncplanner.plannerator.planner.Core;
import static org.lwjgl.opengl.GL20.*;
public class Shader{
    public final int shaderID;
    public Shader(String vertexShaderPath, String fragmentShaderPath){
        shaderID = createShaderProgram(vertexShaderPath, fragmentShaderPath);
    }
    private static int compileShader(int shaderType, String filename){
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, loadFile("/shaders/"+filename));
        glCompileShader(shader);

        int[] success = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, success);

        if(success[0]==0){
            String typeS = shaderType+"";
            if(shaderType==GL_VERTEX_SHADER)typeS = "VERTEX";
            if(shaderType==GL_FRAGMENT_SHADER)typeS = "FRAGMENT";
            throw new RuntimeException(typeS+" Shader compilation failed: "+glGetShaderInfoLog(shader));
        }
        return shader;
    }
    private static int createShaderProgram(int vertexShader, int fragmentShader){
        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        int[] success = new int[1];
        glGetProgramiv(program, GL_LINK_STATUS, success);

        if(success[0]==0){
            throw new RuntimeException("Shader program link failed: "+glGetProgramInfoLog(program));
        }
        return program;
    }
    private static int createShaderProgram(String vertex, String fragment){
        int vertexShader = compileShader(GL_VERTEX_SHADER, vertex);
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragment);
        int program = createShaderProgram(vertexShader, fragmentShader);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        return program;
    }
    private static String loadFile(String path){
        try{
            String s = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(Core.getInputStream(path)));
            String line;
            while((line = reader.readLine())!=null){
                s+="\n"+line;
            }
            reader.close();
            return s.isEmpty()?s:s.substring(1);
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    public void setUniform4f(String varName, float x, float y, float z, float w){
        glUniform4f(glGetUniformLocation(shaderID, varName), x, y, z, w);
    }
    public void setUniform1i(String varName, int x){
        glUniform1i(glGetUniformLocation(shaderID, varName), x);
    }
}
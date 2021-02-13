package planner.tutorial;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import planner.Main;
import planner.file.InputStreamProvider;
public class TutorialFileReader{
    public static final ArrayList<TutorialFormatReader> formats = new ArrayList<>();
    static{
        formats.add(new TutorialFormatReader() {
            @Override
            public boolean formatMatches(InputStream stream){
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))){
                    String line = reader.readLine();
                    if(line==null)return false;
                    if(line.equals("ncpt 1"))return true;
                }catch(IOException ex){}
                return false;
            }
            @Override
            public Tutorial read(InputStream stream){
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))){
                    String line;
                    boolean hitVersionSpecification = false;
                    NCPTTutorial tutorial = null;
                    boolean live = false;
                    int lineNum = 0;
                    while((line = reader.readLine())!=null){
                        lineNum++;
                        String ln = " (line "+lineNum+")";
                        String[] args = line.split(" ");
                        String[] cmd1 = null,cmd2 = null;
                        String arg = null,arg1 = null,arg2 = null;
                        if(args.length>1){
                            cmd1 = line.split(" ", 2);
                            arg = cmd1[1];
                        }//command, arg
                        if(args.length>2){
                            cmd2 = line.split(" ", 3);
                            arg1 = cmd2[1];
                            arg2 = cmd2[2];
                        }//command, arg1, arg2
                        String cmd = args[0];
                        if(cmd.startsWith("#"))continue;//comment
                        //a bit inefficient, but it makes it easier later
                        switch(cmd){//init commands
                            case "ncpt":
                                if(hitVersionSpecification)throw new IllegalArgumentException("NCPT version must be set on line 1!"+ln);
                                hitVersionSpecification = true;
                                continue;
                            case "title":
                                if(tutorial==null){
                                    tutorial = new NCPTTutorial(arg);
                                }else{
                                    throw new IllegalArgumentException("Title has already been set!"+ln);
                                }
                                continue;
                            case "live":
                                if(live)throw new IllegalArgumentException("The tutorial can only me marked live once!"+ln);
                                live = true;
                                continue;
                            default:
                                if(!hitVersionSpecification)throw new IllegalArgumentException("NCPT version has not been set!"+ln);
                                if(tutorial==null)throw new IllegalArgumentException("Title has not been set!"+ln);
                                break;
                        }
                        switch(cmd){//actual commands
                            case "margin"://TODO command and argument validation
                                tutorial.setMargin(Float.parseFloat(arg1), Float.parseFloat(arg2));
                                break;
                            case "translate":
                                tutorial.translate(Float.parseFloat(arg));
                                break;
                            case "ltext":
                                tutorial.ltext(Float.parseFloat(arg1), arg2);
                                break;
                            case "text":
                                tutorial.text(Float.parseFloat(arg1), arg2);
                                break;
                            case "columns":
                                tutorial.columns(Integer.parseInt(arg));
                                break;
                            case "imgsq":
                                tutorial.squareImage(arg);
                                break;
                            case "special":
                                tutorial.special(arg1, Boolean.valueOf(arg2));
                                break;
                            case "skip":
                                tutorial.skip();
                                break;
                            default:
                                throw new IllegalArgumentException("unknown command "+cmd+"!"+ln);
                        }
                    }
                    tutorial.live = live;
                    return tutorial;
                }catch(IOException ex){
                    throw  new RuntimeException(ex);
                }
            }
        });//.ncpt version 1
    }
    public static Tutorial read(InputStreamProvider provider){
        for(TutorialFormatReader reader : formats){
            boolean matches = false;
            try{
                if(reader.formatMatches(provider.getInputStream()))matches = true;
            }catch(Throwable t){}
            if(matches)return reader.read(provider.getInputStream());
        }
        throw new IllegalArgumentException("Unknown file format!");
    }
    public static Tutorial read(File file){
        return read(() -> {
            try{
                return new FileInputStream(file);
            }catch(FileNotFoundException ex){
                return null;
            }
        });
    }
    static Tutorial read(String path){
        return new UpdatingTutorial(() -> {
            return getInputStream(path);
        });
    }
    public static InputStream getInputStream(String path){
        try{
            if(new File("nbproject").exists()){
                return new FileInputStream(new File("src/"+path.replace("/", "/")));
            }else{
                JarFile jar = new JarFile(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ")));
                Enumeration enumEntries = jar.entries();
                while(enumEntries.hasMoreElements()){
                    JarEntry file = (JarEntry)enumEntries.nextElement();
                    if(file.getName().equals(path.replace("/", "/"))){
                        return jar.getInputStream(file);
                    }
                }
            }
            throw new IllegalArgumentException("Cannot find file: "+path);
        }catch(IOException ex){
            System.err.println("Couldn't read file: "+path);
            return null;
        }
    }
}

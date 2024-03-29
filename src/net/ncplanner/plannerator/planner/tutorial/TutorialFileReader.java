package net.ncplanner.plannerator.planner.tutorial;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.ncplanner.plannerator.planner.Core;
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
    public static Tutorial read(Supplier<InputStream> provider){
        for(TutorialFormatReader reader : formats){
            boolean matches = false;
            try{
                if(reader.formatMatches(provider.get()))matches = true;
            }catch(Throwable t){}
            if(matches)return reader.read(provider.get());
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
    public static Tutorial read(String path){
        return new UpdatingTutorial(() -> {
            return Core.getInputStream(path);
        });
    }
}

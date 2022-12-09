package net.ncplanner.plannerator.planner.dssl.token.keyword;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackString;
public class ImportKeyword extends Keyword{
    public ImportKeyword(){
        super("import");
    }
    @Override
    public Keyword newInstance(){
        return new ImportKeyword();
    }
    @Override
    public void run(Script script){
        StackString str = script.pop().asString();
        File f = new File("dssl/"+str.getValue());
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)))){
            String allTheText = "";
            String line;
            while((line = reader.readLine())!=null){
                allTheText+="\n"+line.replace("\t", "    ");//TODO adjustable number of spaces
            }
            Script s = new Script(script.stack, script.variables, allTheText, script.out);
            s.run(null);
            script.variables.putAll(s.variables);
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    @Override
    public KeywordFlavor getFlavor(){
        return KeywordFlavor.KEYWORD;
    }
}
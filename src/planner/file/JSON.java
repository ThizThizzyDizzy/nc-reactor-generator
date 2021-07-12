package planner.file;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import simplelibrary.Queue;
public class JSON{
    public static boolean debug = false;
    public static JSONObject parse(String str) throws IOException{
        Queue<Character> json = new Queue<>();
        for(char c : str.toCharArray())json.enqueue(c);
        return new JSONObject(json);
    }
    public static JSONObject parse(File file) throws IOException{
        return parse(new FileInputStream(file));
    }
    public static JSONObject parse(InputStream stream){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))){
            Queue<Character> json = new Queue<>();
            int c;
            if(debug)System.out.print("Reading JSON file...");
            while((c = reader.read())!=-1){
                json.enqueue((char)c);
            }
            if(debug)System.out.println("Done");
            if(debug)System.out.println("Parsing file...");
            JSONObject obj = new JSONObject(json);
            if(debug)System.out.println("Finished Parsing!");
            return obj;
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    public static class JSONObject extends HashMap<String, Object>{
        public JSONObject(){}
        private JSONObject(Queue<Character> json) throws IOException{
            parse(json);
        }
        private void parse(Queue<Character> json) throws IOException{
            trim(json);
            if(json.dequeue()!='{')throw new IOException("'{' expected!");
            while(!json.isEmpty()){
                trim(json, ',');
                if(json.peek()=='}'){
                    if(debug)System.out.println("--Reached End of object");
                    json.dequeue();
                    break;
                }
                if(json.peek()=='\"'){
                    json.dequeue();
                    String key = "";
                    while(true){
                        while(json.peek()=='\\'){
                            key+=json.dequeue();
                            key+=json.dequeue();
                        }
                        if(json.peek()=='\"'){
                            json.dequeue();
                            break;
                        }
                        key+=json.dequeue();
                    }
                    trim(json);
                    if(json.peek()!=':')throw new IOException("':' expected");
                    json.dequeue();//remove the :
                    trim(json);
                    //what's this new entry?
                    if(json.peek()=='\"'){//it's a string!
                        json.dequeue();
                        String value = "";
                        while(true){
                            while(json.peek()=='\\'){
                                value+=json.dequeue();
                                value+=json.dequeue();
                            }
                            if(json.peek()=='\"'){
                                json.dequeue();
                                break;
                            }
                            value+=json.dequeue();
                        }
                        put(key.replace("\\\"", "\""), value.replace("\\\"", "\""));
                        if(debug)System.out.println("Found new entry: \""+key+"\": \""+value+"\"");
                    }else if(json.peek()=='{'){
                        //it's an object!
                        if(debug)System.out.println("Found new object: \""+key+"\"");
                        JSONObject newObject = new JSONObject(json);
                        put(key.replace("\\\"", "\""), newObject);
                    }else if(Character.isDigit(json.peek())||json.peek()=='-'||json.peek()=='.'){
                        //it's a number!
                        String num = "";
                        while(Character.isDigit(json.peek())||json.peek()=='.'||json.peek()=='-'){
                            num+=json.dequeue();
                        }
                        if(num.startsWith("-."))throw new IOException("Numbers must contain at least one digit before the decimal point!");
                        if(json.peek()=='e'||json.peek()=='E'){
                            //exponent, gosh darnit!
                            num+=Character.toUpperCase(json.dequeue());
                            while(Character.isDigit(json.peek())||json.peek()=='.'||json.peek()=='-'||json.peek()=='+'){
                                num+=json.dequeue();
                            }
                        }
                        Object value;
                        if(json.peek()=='f'||json.peek()=='F'){
                            value = Float.parseFloat(num);
                            json.dequeue();
                        }else if(json.peek()=='d'||json.peek()=='D'){
                            value = Double.parseDouble(num);
                            json.dequeue();
                        }else if(json.peek()=='b'||json.peek()=='B'){
                            value = Byte.parseByte(num);
                            json.dequeue();
                        }else if(json.peek()=='s'||json.peek()=='S'){
                            value = Short.parseShort(num);
                            json.dequeue();
                        }else if(json.peek()=='l'||json.peek()=='L'){
                            value = Long.parseLong(num);
                            json.dequeue();
                        }else if(json.peek()=='i'||json.peek()=='I'){
                            value = Integer.parseInt(num);
                            json.dequeue();
                        }else if(num.contains(".")){
                            value = Double.parseDouble(num);
                        }else{
                            value = Integer.parseInt(num);
                        }
                        put(key.replace("\\\"", "\""), value);
                        if(debug)System.out.println("Found new entry: \""+key+"\": "+value);
                    }else if(json.peek()=='['){
                        //it's an array!
                        if(debug)System.out.println("Found new array: \""+key+"\"");
                        JSONArray newArray = new JSONArray(json);
                        put(key.replace("\\\"", "\""), newArray);
                    }else if(json.peek()=='t'||json.peek()=='T'){
                        boolean yay = false;
                        json.dequeue();
                        if(json.peek()=='r'||json.peek()=='R'){
                            json.dequeue();
                            if(json.peek()=='u'||json.peek()=='U'){
                                json.dequeue();
                                if(json.peek()=='e'||json.peek()=='E'){
                                    json.dequeue();
                                    yay = true;
                                }
                            }
                        }
                        if(!yay)throw new IOException("Failed to parse JSON file: Unknown entry - "+sub(json, 25)+"...");
                        put(key.replace("\\\"", "\""), true);
                    }else if(json.peek()=='f'||json.peek()=='F'){
                        boolean yay = false;
                        json.dequeue();
                        if(json.peek()=='a'||json.peek()=='A'){
                            json.dequeue();
                            if(json.peek()=='l'||json.peek()=='L'){
                                json.dequeue();
                                if(json.peek()=='s'||json.peek()=='S'){
                                    json.dequeue();
                                    if(json.peek()=='e'||json.peek()=='E'){
                                        json.dequeue();
                                        yay = true;
                                    }
                                }
                            }
                        }
                        if(!yay)throw new IOException("Failed to parse JSON file: Unknown entry - "+sub(json, 25)+"...");
                        put(key.replace("\\\"", "\""), false);
                    }else{
                        throw new IOException("Failed to parse JSON file: Unknown entry - "+sub(json,25)+"...");
                    }
                    continue;
                }
                throw new IOException("Failed to parse JSON file: I don't know what this is! - "+sub(json,25)+"...");
            }
        }
        public void write(File file) throws IOException{
            write(new FileOutputStream(file));
        }
        public void write(OutputStream stream) throws IOException{
            try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream))){
                writer.write(write());
            }
        }
        private String write(){
            if(isEmpty()){
                return "{}";
            }
            String json = "{";
            for(String str : keySet()){
                json+="\""+str+"\":";
                Object o = get(str);
                if(o instanceof String){
                    json+="\""+(String)o+"\"";
                }else if(o instanceof Boolean){
                    json+=(Boolean)o;
                }else if(o instanceof Double){
                    json+=(Double)o;
                }else if(o instanceof Integer){
                    json+=(Integer)o;
                }else if(o instanceof Long){
                    json+=(Long)o;
                }else if(o instanceof Float){
                    json+=(Float)o;
                }else if(o instanceof JSONObject){
                    json+=((JSONObject)o).write();
                }else if(o instanceof JSONArray){
                    json+=((JSONArray)o).write();
                }
                json+=",";
            }
            json = json.substring(0, json.length()-1)+"}";
            return json;
        }
        public JSONObject getJSONObject(String key){
            Object o = get(key);
            if(o==null)return null;
            if(o instanceof JSONObject)return (JSONObject)o;
            return null;
        }
        public JSONArray getJSONArray(String key){
            Object o = get(key);
            if(o==null)return null;
            if(o instanceof JSONArray)return (JSONArray)o;
            return null;
        }
        public String getString(String key){
            Object o = get(key);
            if(o==null)return null;
            if(o instanceof String)return (String)o;
            return null;
        }
        public Boolean getBoolean(String key){
            Object o = get(key);
            if(o==null)return null;
            if(o instanceof Boolean)return (Boolean)o;
            return null;
        }
        public Double getDouble(String key){
            Object o = get(key);
            if(o==null)return null;
            if(o instanceof Number)return ((Number)o).doubleValue();
            return null;
        }
        public Float getFloat(String key){
            Object o = get(key);
            if(o==null)return null;
            if(o instanceof Number)return ((Number)o).floatValue();
            return null;
        }
        public Integer getInt(String key){
            Object o = get(key);
            if(o==null)return null;
            if(o instanceof Number)return ((Number)o).intValue();
            return null;
        }
        public Long getLong(String key){
            Object o = get(key);
            if(o==null)return null;
            if(o instanceof Number)return ((Number)o).longValue();
            return null;
        }
        //because I like set instead of put
        public Object set(String key, Object value){
            return put(key, value);
        }
        @Override
        public String toString(){
            return write();
        }
    }
    public static class JSONArray extends ArrayList<Object>{
        public JSONArray(){}
        private JSONArray(Queue<Character> json) throws IOException{
            parse(json);
        }
        private void parse(Queue<Character> json) throws IOException{
            if(json.dequeue()!='[')throw new IOException("'[' expected!");
            while(!json.isEmpty()){
                trim(json, ',');
                if(json.peek()=='['){
                    //it's an array!
                    if(debug)System.out.println("Found new array!");
                    json.dequeue();
                    JSONArray newArray = new JSONArray(json);
                    add(newArray);
                }else if(json.peek()==']'){
                    if(debug)System.out.println("-Reached End of array");
                    json.dequeue();
                    break;
                }else if(json.peek()=='\"'){
                    //it's a string!
                    json.dequeue();
                    String value = "";
                    while(true){
                        while(json.peek()=='\\'){
                            value+=json.dequeue();
                            value+=json.dequeue();
                        }
                        if(json.peek()=='\"'){
                            json.dequeue();
                            break;
                        }
                        value+=json.dequeue();
                    }
                    add(value.replace("\\\"", "\""));
                    if(debug)System.out.println("Found new item: \""+value+"\"");
                }else if(json.peek()=='{'){
                    //it's an object!
                    if(debug)System.out.println("Found new object!");
                    JSONObject newObject = new JSONObject(json);
                    add(newObject);
                }else if(Character.isDigit(json.peek())||json.peek()=='-'||json.peek()=='.'){
                    //it's a number!
                    String num = "";
                    while(Character.isDigit(json.peek())||json.peek()=='.'||json.peek()=='-'){
                        num+=json.dequeue();
                    }
                    if(num.startsWith("-."))throw new IOException("Numbers must contain at least one digit before the decimal point!");
                    if(json.peek()=='e'||json.peek()=='E'){
                        //exponent, gosh darnit!
                        num+=Character.toUpperCase(json.dequeue());
                        while(Character.isDigit(json.peek())||json.peek()=='.'||json.peek()=='-'||json.peek()=='+'){
                            num+=json.dequeue();
                        }
                    }
                    Object value;
                    if(json.peek()=='f'||json.peek()=='F'){
                        value = Float.parseFloat(num);
                        json.dequeue();
                    }else if(json.peek()=='d'||json.peek()=='D'){
                        value = Double.parseDouble(num);
                        json.dequeue();
                    }else if(json.peek()=='b'||json.peek()=='B'){
                        value = Byte.parseByte(num);
                        json.dequeue();
                    }else if(json.peek()=='s'||json.peek()=='S'){
                        value = Short.parseShort(num);
                        json.dequeue();
                    }else if(json.peek()=='l'||json.peek()=='L'){
                        value = Long.parseLong(num);
                        json.dequeue();
                    }else if(json.peek()=='i'||json.peek()=='I'){
                        value = Integer.parseInt(num);
                        json.dequeue();
                    }else if(num.contains(".")){
                        value = Double.parseDouble(num);
                    }else{
                        value = Integer.parseInt(num);
                    }
                    add(value);
                    if(debug)System.out.println("Found new item: "+value);
                }else if(json.peek()=='t'||json.peek()=='T'){
                    boolean yay = false;
                    json.dequeue();
                    if(json.peek()=='r'||json.peek()=='R'){
                        json.dequeue();
                        if(json.peek()=='u'||json.peek()=='U'){
                            json.dequeue();
                            if(json.peek()=='e'||json.peek()=='E'){
                                json.dequeue();
                                yay = true;
                            }
                        }
                    }
                    if(!yay)throw new IOException("Failed to parse JSON file: Unknown entry - "+sub(json, 25)+"...");
                    add(true);
                    if(debug)System.out.println("Found new item: true");
                }else if(json.peek()=='f'||json.peek()=='F'){
                    boolean yay = false;
                    json.dequeue();
                    if(json.peek()=='a'||json.peek()=='A'){
                        json.dequeue();
                        if(json.peek()=='l'||json.peek()=='L'){
                            json.dequeue();
                            if(json.peek()=='s'||json.peek()=='S'){
                                json.dequeue();
                                if(json.peek()=='e'||json.peek()=='E'){
                                    json.dequeue();
                                    yay = true;
                                }
                            }
                        }
                    }
                    if(!yay)throw new IOException("Failed to parse JSON file: Unknown entry - "+sub(json, 25)+"...");
                    add(false);
                    if(debug)System.out.println("Found new item: false");
                }else{
                    throw new IOException("Failed to parse JSON file: Unknown entry - "+sub(json,25)+"...");
                }
            }
        }
        private String write(){
            String json = "[";
            for(Object o : this){
                if(o instanceof String){
                    json+="\""+(String)o+"\"";
                }else if(o instanceof Boolean){
                    json+=(Boolean)o;
                }else if(o instanceof Double){
                    json+=(Double)o;
                }else if(o instanceof Integer){
                    json+=(Integer)o;
                }else if(o instanceof Long){
                    json+=(Long)o;
                }else if(o instanceof Float){
                    json+=(Float)o;
                }else if(o instanceof JSONObject){
                    json+=((JSONObject)o).write();
                }else if(o instanceof JSONArray){
                    json+=((JSONArray)o).write();
                }
                json+=",";
            }
            if(json.contains(","))json = json.substring(0, json.length()-1);
            json = json+"]";
            return json;
        }
        @Override
        public String toString(){
            return write();
        }
    }
    private static String sub(Queue<Character> json, int limit){
        if(json.isEmpty())return "";
        String s = "";
        limit = Math.min(limit, json.size());
        for(int i = 0; i<limit; i++){
            s+=json.dequeue();
        }
        return s;
    }
    private static void trim(Queue<Character> json){
        char c = json.peek();
        while(c==' '||c=='\n'||c=='\r'||c=='\t'){
            json.dequeue();
            c = json.peek();
        }
    }
    private static void trim(Queue<Character> str, char... chars){
        WHILE:while(true){
            char c = str.peek(); 
            if(Character.isWhitespace(c)){
                str.dequeue();
                continue;
            }
            for(char ch : chars){
                if(c==ch){
                    str.dequeue();
                    continue WHILE;
                }
            }
            break;
        }
    }
}
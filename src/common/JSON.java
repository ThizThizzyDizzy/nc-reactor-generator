package common;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
public class JSON{
    public static boolean debug = false;
    public static JSONObject parse(String str) throws IOException{
        return new JSONObject(str);
    }
    public static JSONObject parse(File file){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
            String json = "";
            String line;
            if(debug)System.out.print("Reading JSON file...");
            while((line = reader.readLine())!=null){
                json+=line.trim()+"\n";
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
        private JSONObject(String json) throws IOException{
            parse(json);
        }
        private String parse(String json) throws IOException{
            while(!json.isEmpty()){
                json = json.trim();
                if(json.startsWith("\n")){
                    //just the end of the last line
                    json = json.substring(1);
                    continue;
                }
                if(json.startsWith("{")){
                    //just the start of this object.
                    json = json.substring(1);
                    continue;
                }
                if(json.startsWith(",")){
                    //just a comma after an object
                    json = json.substring(1);
                    continue;
                }
                if(json.startsWith("}")){
                    if(debug)System.out.println("--Reached End of object");
                    json = json.substring(1);
                    break;
                }
                if(json.startsWith("\"")){
                    int c = json.indexOf(':');
                    String key = json.substring(1, c-1);
                    json = json.substring(c+1).trim();
                    //what's this new entry?
                    if(json.startsWith("\"")){
                        //it's a string!
                        json = json.substring(1);
                        c = json.replaceAll("\\Q\\\"", ":D").indexOf('"');
                        String value = json.substring(0, c);
                        put(key, value.replaceAll("\\Q\\\"", "\""));
                        if(debug)System.out.println("Found new entry: \""+key+"\": \""+value+"\"");
                        json = json.substring(c+1);
                    }else if(json.startsWith("{")){
                        //it's an object!
                        if(debug)System.out.println("Found new object: \""+key+"\"");
                        JSONObject newObject = new JSONObject();
                        json = newObject.parse(json);
                        put(key, newObject);
                    }else if(Character.isDigit(json.charAt(0))||json.charAt(0)=='.'||json.charAt(0)=='-'){
                        if(json.startsWith(".")||json.startsWith("-.")){
                            throw new IOException("Decimals must have a whole number! ..."+json);
                        }
                        //it's a number!
                        String num = "";
                        while(Character.isDigit(json.charAt(0))||json.charAt(0)=='.'||json.charAt(0)=='-'){
                            num+=json.charAt(0);
                            json = json.substring(1);
                        }
                        if(json.charAt(0)=='e'||json.charAt(0)=='E'){
                            //exponent, gosh darnit!
                            json = json.substring(1);
                            num+="E";
                            while(Character.isDigit(json.charAt(0))||json.charAt(0)=='.'||json.charAt(0)=='-'||json.charAt(0)=='+'){
                                num+=json.charAt(0);
                                json = json.substring(1);
                            }
                        }
                        Object value;
                        if(json.startsWith("f")){
                            value = Float.parseFloat(num);
                            json = json.substring(1);
                        }else if(json.startsWith("d")||num.contains(".")){
                            value = Double.parseDouble(num);
                            if(json.startsWith("d"))json = json.substring(1);
                        }else if(json.startsWith("b")){
                            value = Byte.parseByte(num);
                            json = json.substring(1);
                        }else if(json.startsWith("s")){
                            value = Short.parseShort(num);
                            json = json.substring(1);
                        }else if(json.startsWith("l")){
                            value = Long.parseLong(num);
                            json = json.substring(1);
                        }else{
                            value = Integer.parseInt(num);
                            if(json.startsWith("i")){
                                json = json.substring(1);
                            }
                        }
                        put(key, value);
                        if(debug)System.out.println("Found new entry: \""+key+"\": "+value);
                    }else if(json.startsWith("[")){
                        //it's an array!
                        if(debug)System.out.println("Found new array: \""+key+"\"");
                        JSONArray newArray = new JSONArray();
                        json = newArray.parse(json.substring(1));
                        put(key, newArray);
                    }else if(json.startsWith("true")||json.startsWith("false")){
                        //it's a boolean!
                        boolean value = json.startsWith("true");
                        if(value){
                            json = json.substring(4);
                        }else{
                            json = json.substring(5);
                        }
                        put(key, value);
                        if(debug)System.out.println("Found new entry: \""+key+"\": "+value);
                    }else{
                        throw new IOException("Failed to parse JSON file: Unknown entry - "+sub(json,25)+"...");
                    }
                    continue;
                }
                throw new IOException("Failed to parse JSON file: I don't know what this is! - "+sub(json,25)+"...");
            }
            return json;
        }
        public void write(File file) throws IOException{
            try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))){
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
        private JSONArray(String json) throws IOException{
            parse(json);
        }
        private String parse(String json) throws IOException{
            while(!json.isEmpty()){
                json = json.trim();
                if(json.startsWith("\n")){
                    //just the end of the last line
                    json = json.substring(1);
                    continue;
                }
                if(json.startsWith("[")){
                    //it's an array!
                    if(debug)System.out.println("Found new array!");
                    JSONArray newArray = new JSONArray();
                    json = newArray.parse(json.substring(1));
                    add(newArray);
                }else if(json.startsWith(",")){
                    //just a comma after an item
                    json = json.substring(1);
                }else if(json.startsWith("]")){
                    if(debug)System.out.println("-Reached End of array");
                    json = json.substring(1);
                    break;
                }else if(json.startsWith("\"")){
                    //it's a string!
                    json = json.substring(1);
                    int c = json.indexOf('"');
                    String value = json.substring(0, c);
                    add(value);
                    if(debug)System.out.println("Found new item: \""+value+"\"");
                    json = json.substring(c+1);
                }else if(json.startsWith("{")){
                    //it's an object!
                    if(debug)System.out.println("Found new object!");
                    JSONObject newObject = new JSONObject();
                    json = newObject.parse(json);
                    add(newObject);
                }else if(Character.isDigit(json.charAt(0))||json.charAt(0)=='.'||json.charAt(0)=='-'){
                    if(json.startsWith(".")||json.startsWith("-.")){
                        throw new IOException("All decimals must start with a whole number! ..."+json);
                    }
                    //it's a number!
                    String num = "";
                    while(Character.isDigit(json.charAt(0))||json.charAt(0)=='.'||json.charAt(0)=='-'){
                        num+=json.charAt(0);
                        json = json.substring(1);
                    }
                    Object value;
                    if(json.startsWith("f")){
                        value = Float.parseFloat(num);
                        json = json.substring(1);
                    }else if(json.startsWith("d")||num.contains(".")){
                        value = Double.parseDouble(num);
                        if(json.startsWith("d"))json = json.substring(1);
                    }else if(json.startsWith("b")){
                        value = Byte.parseByte(num);
                        json = json.substring(1);
                    }else if(json.startsWith("s")){
                        value = Short.parseShort(num);
                        json = json.substring(1);
                    }else if(json.startsWith("l")){
                        value = Long.parseLong(num);
                        json = json.substring(1);
                    }else{
                        value = Integer.parseInt(num);
                        if(json.startsWith("i")){
                            json = json.substring(1);
                        }
                    }
                    add(value);
                    if(debug)System.out.println("Found new item: "+value);
                }else if(json.startsWith("true")||json.startsWith("false")){
                    //it's a boolean!
                    boolean value = json.startsWith("true");
                    if(value){
                        json = json.substring(4);
                    }else{
                        json = json.substring(5);
                    }
                    add(value);
                    if(debug)System.out.println("Found new item: "+value);
                }else{
                    throw new IOException("Failed to parse JSON file: Unknown entry - "+sub(json,25)+"...");
                }
            }
            return json;
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
    private static String sub(String json, int limit){
        if(json.isEmpty())return json;
        return json.substring(0,Math.min(json.length(), limit));
    }
}

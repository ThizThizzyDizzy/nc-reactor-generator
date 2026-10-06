package net.ncplanner.plannerator.planner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
public class Main{
    public static boolean isBot = false;
    public static boolean headless = false;
    public static boolean novr = false;
    public static String discordBotToken;
    public static boolean benchmark = false;
    public static boolean justUpdated = false;
    public static boolean isMacOS;
    private static boolean ensureLibraries(String[] args){
        ArrayList<String> libraries = new ArrayList<>();
        try{
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/net/ncplanner/plannerator/library-manifest.txt")))){
                String line;
                while((line = reader.readLine())!=null){
                    if(line.isBlank())continue;
                    libraries.add(line);
                }
            }
        }catch(Exception ex){
            System.err.println("WARNING: UNABLE TO READ library-manifest.txt! LIBRARY DOWNLOADING HAS BEEN SKIPPED! ("+ex.toString()+")");
            return true;
        }
        boolean librariesChanged = false;
        File libRoot = new File("lib").getAbsoluteFile();
        libRoot.mkdirs();
        for(String library : libraries){
            File f = new File(libRoot, new File(library).getName());
            System.out.println("Checking library: "+library);
            if(f.exists())continue;
            String url = "https://github.com/ThizThizzyDizzy/nc-reactor-generator/raw/overhaul/libraries/"+library;
            System.out.println("Downloading: "+library+" ("+url+")");
            HttpURLConnection conn;
            try{
                conn = (HttpURLConnection)new URI(url).toURL().openConnection();
                conn.setRequestMethod("GET");
                if(conn.getResponseCode()==200){
                    try(InputStream in = conn.getInputStream()){
                        librariesChanged = true;
                        Files.copy(in, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }catch(Exception ex){
                System.err.println("Failed to download library: "+library+" ("+ex.toString()+")");
            }
        }
        librariesChanged |= ensureGithubLibrary("ThizThizzyDizzy", "Dizzy-Engine", "DizzyEngine", "v0.1.0");
        librariesChanged |= ensureGithubLibrary("ThizThizzyDizzy", "NCPF", "NCPF", "v0.1.0");
        return librariesChanged;
    }
    public static boolean ensureGithubLibrary(String repoOwner, String repoName, String fileName, String version){
        String trimmedVersion = version;
        while(Character.isAlphabetic(trimmedVersion.charAt(0))&&!trimmedVersion.isEmpty())
            trimmedVersion = trimmedVersion.substring(1);
        if(trimmedVersion.isEmpty())trimmedVersion = version;
        File f = new File("lib"+File.separator+fileName+".jar").getAbsoluteFile();
        System.out.println("Checking Github library: "+f.getName());
        if(f.exists())return false;

        String link = fetchGithubDownloadLink(repoOwner, repoName, fileName, version);
        if(link==null){
            System.err.println("Unable to find Github library: "+fileName+" "+version+" under "+repoOwner+"/"+repoName);
            return false;
        }

        System.out.println("Downloading Github Library: "+f.getName()+" ("+link+")");
        HttpURLConnection conn;
        try{
            conn = (HttpURLConnection)new URI(link).toURL().openConnection();
            conn.setRequestMethod("GET");
            if(conn.getResponseCode()==200){
                try(InputStream in = conn.getInputStream()){
                    Files.copy(in, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    return true;
                }
            }
        }catch(Exception ex){
            System.err.println("Failed to download Github library: "+fileName+" "+version+" under "+repoOwner+"/"+repoName);
        }
        return false;
    }
    private static String fetchGithubDownloadLink(String repoOwner, String repoName, String fileBaseName, String version){
        String url = "https://api.github.com/repos/"+repoOwner+"/"+repoName+"/releases/tags/"+version;
        HttpURLConnection conn;
        try{
            conn = (HttpURLConnection)new java.net.URL(url).openConnection();
            conn.setRequestMethod("GET");
            if(conn.getResponseCode()==200){
                try(java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()))){
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while((line = reader.readLine())!=null){
                        sb.append(line);
                    }
                    String json = sb.toString();

                    java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\{[^\\{]*\"name\":\"([^\"]+)\"[^\\{]*\"browser_download_url\":\"([^\"]+)\"[^\\}]*\\}");
                    java.util.regex.Matcher m = p.matcher(json);

                    while(m.find()){
                        String name = m.group(1);
                        if(!name.endsWith(".jar")&&!name.contains(fileBaseName))
                            continue;
                        return m.group(2);
                    }
                }
            }
        }catch(Exception ex){
            System.err.println("Unable to fetch GitHub release: "+url);
        }
        return null;
    }
    public static void main(String[] args){
        isMacOS = System.getProperty("os.name").toLowerCase().contains("mac");
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/net/ncplanner/plannerator/version.properties")))){
            String line;
            while((line = reader.readLine())!=null){
                String[] parts = line.split("=");
                if("bundled".equals(parts[0])&&"false".equals(parts[1])){
                    if(ensureLibraries(args)){
                        restartApplication();
                        return;
                    }
                    break;
                }
            }
        }catch(IOException ex){
            System.err.println("WARNING: UNABLE TO READ versions.properties! LIBRARY DOWNLOADING HAS BEEN SKIPPED! ("+ex.toString()+")");
        }
        FOR:
        for(int i = 0; i<args.length; i++){
            switch(args[i]){
                case "justUpdated":
                    justUpdated = true;
                    break;
                case "headless":
                    headless = true;
                    break;
                case "novr":
                    novr = true;
                    break;
                case "benchmark":
                    benchmark = true;
                    break FOR;
                case "maybediscord":
                    System.out.println("Bot or Planner? (B|P)\n> ");
                    try(BufferedReader r = new BufferedReader(new InputStreamReader(System.in))){
                        String s = r.readLine();
                        if(s==null)s = "";
                        s = s.trim();
                        if(s.equalsIgnoreCase("B")||s.equalsIgnoreCase("Bot")||s.equalsIgnoreCase("Discord"))
                            args[i] = "discord";
                    }catch(IOException ex){
                        System.out.println("Could not read input! ("+ex.getClass().getName()+": "+ex.getMessage()+") - running as planner.");
                    }
                case "discord":
                    if(args[i].equals("discord")){
                        isBot = true;
                        discordBotToken = args[i+1];
                    }
                    break;
            }
        }
        System.out.println("Initializing...");
        Core.main(args);
    }
    public static void restartApplication() throws IOException{
        restartApplication(null);
    }
    public static void restartApplication(String jarPath) throws IOException{
        String javaBin = System.getProperty("java.home")+File.separator+"bin"+File.separator+"java";

        String commandProperty = System.getProperty("sun.java.command");
        if(commandProperty==null){
            throw new IOException("Failed to flag the launch command.");
        }

        String[] commandParts = commandProperty.split(" ");
        if(jarPath==null)jarPath = commandParts[0];

        ArrayList<String> command = new ArrayList<>();
        command.add(javaBin);

        List<String> jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();

        if(isMacOS&&!command.contains("-XstartOnFirstThread")){
            command.add("-XstartOnFirstThread");
        }

        command.add("-jar");
        command.add(jarPath);

        command.add("justUpdated"); // ehh, this is actually if it just downloaded libraries...
        for(int i = 1; i<commandParts.length; i++){
            command.add(commandParts[i]);
        }

        new ProcessBuilder(command).inheritIO().start();
        System.exit(0);
    }
    public static void generateCrashReport(String message, Throwable ex){
        GregorianCalendar calendar = new GregorianCalendar();
        File file = new File("crash-reports"+File.separatorChar+"crash-"+calendar.getTime().toString().replace(":", "-")+".txt");
        if(!file.getParentFile().exists())file.getParentFile().mkdirs();
        int i = 1;
        while(file.exists()){
            file = new File("crash-reports"+File.separatorChar+"crash-"+calendar.getTime().toString().replace(":", "-")+"_"+i+".txt");
            i++;
        }
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))){
            writer.write("Planner version "+Core.updater.currentVersion+"\n");
            writer.write("OS: "+System.getProperty("os.name")+" ("+System.getProperty("os.arch")+")\n\n");
            writer.write(message+"\n");
            if(ex!=null){
                writer.write(ex.getClass().getName()+": "+ex.getMessage()+"\n");
                for(StackTraceElement e : ex.getStackTrace()){
                    writer.write(" at "+e.toString()+"\n");
                }
                Throwable throwable = ex;
                while(throwable.getCause()!=null){
                    throwable = throwable.getCause();
                    writer.write("Caused by "+throwable.getClass().getName()+": "+throwable.getMessage()+"\n");
                    for(StackTraceElement e : throwable.getStackTrace()){
                        writer.write(" at "+e.toString()+"\n");
                    }
                }
            }
            writer.write("\n"+Core.getCrashReportData()+"\n");
            writer.write("Threads: \n");
            Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
            for(Thread t : threads.keySet()){
                writer.write(t.getName()+": "+t.getState().toString()+(t.isDaemon()?" [D]":"")+"\n");
                for(StackTraceElement e : threads.get(t)){
                    writer.write("  "+e.toString()+"\n");
                }
            }
        }catch(IOException e){
        }
    }
}

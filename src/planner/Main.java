package planner;
import discord.Bot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
public class Main{
    private static String requiredSimpleLibraryVersion = "11.0.0";
    private static int simplelibrarySize = 560;
    private static int simplelibraryExtendedSize = 628;
    /**
     * Set to "" for latest, otherwise exact version #
     */
    private static String requiredSimpleLibraryExtendedVersion = null;
    private static String versionListURL = "https://www.dropbox.com/s/0jezlrj26wmcurz/versions.txt?dl=1";
    public static final String applicationName = "Nuclearcraft Reactor Generator";
    private static HashMap<String[], Integer> requiredLibraries = new HashMap<>();
    private static int downloadSize = 0;
    //Download details
    private static int total;
    private static int current;
    private static JFrame frame;
    private static JProgressBar bar;
    private static boolean allowDownload = true;
    public static int os;
    public static final int OS_UNKNOWN = -1;
    public static final int OS_WINDOWS = 0;
    public static final int OS_SOLARIS = 1;
    public static final int OS_MACOSX = 2;
    public static final int OS_LINUX = 3;
    public static final int BIT_UNKNOWN = -1;
    public static final int BIT_32 = 0;
    public static final int BIT_64 = 1;
    public static boolean isBot = false;
    private static void addRequiredLibrary(String url, String filename, int sizeKB){
        requiredLibraries.put(new String[]{url,filename}, sizeKB);
    }
    public static void main(String[] args) throws NoSuchMethodException, IOException, InterruptedException, URISyntaxException{
        if(args.length>=1&&args[0].equals("discord")||args.length>=2&&args[1].equals("discord")){
            addRequiredLibrary("https://www.dropbox.com/s/zeeu5wgmcisg4ez/JDA-4.1.1_101.jar?dl=1", "JDA-4.1.1_101.jar", 1097);
            addRequiredLibrary("https://www.dropbox.com/s/ljx8in7xona4akl/annotations-16.0.1.jar?dl=1", "annotations-16.0.1.jar", 19);
            addRequiredLibrary("https://www.dropbox.com/s/5fzv4attffxpn67/commons-collections4-4.1.jar?dl=1", "commons-collections4-4.1.jar", 734);
            addRequiredLibrary("https://www.dropbox.com/s/w9ca19hm60az7d6/jackson-annotations-2.10.1.jar?dl=1", "jackson-annotations-2.10.1.jar", 67);
            addRequiredLibrary("https://www.dropbox.com/s/glbpufagd0mpr1c/jackson-core-2.10.1.jar?dl=1", "jackson-core-2.10.1.jar", 341);
            addRequiredLibrary("https://www.dropbox.com/s/djfkcwgily1xqah/jackson-databind-2.10.1.jar?dl=1", "jackson-databind-2.10.1.jar", 1371);
            addRequiredLibrary("https://www.dropbox.com/s/dkg097yp0sm1d6l/jna-4.4.0.jar?dl=1", "jna-4.4.0.jar", 1066);
            addRequiredLibrary("https://www.dropbox.com/s/a9fil1c2z6fkzav/jsr305-3.0.2.jar?dl=1", "jsr305-3.0.2.jar", 20);
            addRequiredLibrary("https://www.dropbox.com/s/1kcxeldni1vr1il/nv-websocket-client-2.9.jar?dl=1", "nv-websocket-client-2.9.jar", 121);
            addRequiredLibrary("https://www.dropbox.com/s/y3oztlbymtx9ldw/okhttp-3.13.0.jar?dl=1", "okhttp-3.13.0.jar", 405);
            addRequiredLibrary("https://www.dropbox.com/s/hom0yvn6htky8nn/okio-1.17.2.jar?dl=1", "okio-1.17.2.jar", 90);
            addRequiredLibrary("https://www.dropbox.com/s/cv7wico9ry711a1/opus-java-api-1.0.4.jar?dl=1", "opus-java-api-1.0.4.jar", 11);
            addRequiredLibrary("https://www.dropbox.com/s/lmlh95nonmfmkx5/opus-java-natives-1.0.4.jar?dl=1", "opus-java-natives-1.0.4.jar", 2228);
            addRequiredLibrary("https://www.dropbox.com/s/1uguzf5hpqzo0qn/slf4j-api-1.7.25.jar?dl=1", "slf4j-api-1.7.25.jar", 41);
            addRequiredLibrary("https://www.dropbox.com/s/ho0vh24y9cizt9x/trove4j-3.0.3.jar?dl=1", "trove4j-3.0.3.jar", 2465);
            isBot = true;
        }
        args = update(args);
        if(args==null){
            return;
        }
        Core.main(args);
    }
    private static String getLibraryRoot(){
        return "libraries";
    }
    private static String[] update(String[] args) throws URISyntaxException, IOException, InterruptedException{
        ArrayList<String> theargs = new ArrayList<>(Arrays.asList(args));
        if(args.length<1||!args[0].equals("Skip Dependencies")){
            if(versionListURL.isEmpty()){
                System.err.println("Version list URL is empty! assuming latest version.");
            }else{
                Updater updater = Updater.read(versionListURL, VersionManager.currentVersion, applicationName);
                if(updater!=null&&updater.getVersionsBehindLatestDownloadable()>0&&(isBot||JOptionPane.showConfirmDialog(null, "Version "+updater.getLatestDownloadableVersion()+" is out!  Would you like to update "+applicationName+" now?", applicationName+" "+VersionManager.currentVersion+"- Update Available", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)){
                    startJava(new String[0], new String[]{"justUpdated"}, updater.update(updater.getLatestDownloadableVersion()));
                    System.exit(0);
                }
            }
            String[][] nativesPaths = {
                {"https://dl.dropboxusercontent.com/s/1nt1g7ui7p4eb54/windows32natives.zip?dl=1&token_hash=AAFsOnqBqipIOxc4sNr138FnlZIjHBf-KPwMTNe8F5lqOQ",
                 "https://dl.dropboxusercontent.com/s/y41peavuls3ptzu/windows64natives.zip?dl=1&token_hash=AAEJ6Ih8HGEsla1tJmIB7R-YBCTC8LVq_D4OFcFWDCEZ5Q"},
                {"https://dl.dropboxusercontent.com/s/h4z3j2pspuos15l/solaris32natives.zip?dl=1&token_hash=AAEmlE84CzHRTqya3xPN9xRh_1_v0nGccJFp-bfru4jSRw",
                 "https://dl.dropboxusercontent.com/s/vq3x3n81x0qvc3u/solaris64natives.zip?dl=1&token_hash=AAEyl6swuFIukpTNZjrgv96TGwSnMYxWt0hdQ71_KiqQqw"},
                {"https://dl.dropboxusercontent.com/s/ljvgoccqz33bcq1/macosx32natives.zip?dl=1&token_hash=AAGezz3pNxqa6Fi_O-xGCZdI2923D7b-ZsrWZ61HlFROYw",
                 null},
                {"https://dl.dropboxusercontent.com/s/nfv4ra6n68lna9n/linux32natives.zip?dl=1&token_hash=AAGzHZLGp9S4HAjzpzNZp9-YixYw4H56D6_DJ3dG5GDeFA",
                 "https://dl.dropboxusercontent.com/s/rp6uhdmec7697ty/linux64natives.zip?dl=1&token_hash=AAHl6tcg11VwWr31WtqMUlozabCSpr0LfS5MLS2MpmWnEA"}
            };
            String OS = System.getenv("OS");
            int whichOS = OS_UNKNOWN;
            if("Windows_NT".equals(OS))whichOS = OS_WINDOWS;
            if(whichOS==OS_UNKNOWN){
                whichOS = JOptionPane.showOptionDialog(null, "Unrecognized OS \""+OS+"\"!\nPlease report this problem on the "+applicationName+" issue tracker.\nIn the meantime, which natives should I load?", "Unrecognized Operating System", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"Windows", "Solaris", "Mac OSX", "Linux"}, "Windows");
                if(whichOS<0||whichOS>3){
                    System.exit(0);
                }
            }
            os = whichOS;
            String version = System.getenv("PROCESSOR_ARCHITECTURE");
            int whichBitDepth = BIT_UNKNOWN;
            if("x86".equals(version))whichBitDepth = BIT_32;
            if("AMD64".equals(version))whichBitDepth = BIT_64;
            if(whichBitDepth==BIT_UNKNOWN){
                whichBitDepth = JOptionPane.showOptionDialog(null, "Unrecognized processor architecture \""+version+"\"!\nPlease report this problem on the "+applicationName+" issue tracker.\nIn the meantime, should I load the 64 bit binaries with the 32 bit ones?", "Unrecognized Processor Architecture", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[]{"No, treat it as a 32 bit system", "Yes, treat it as a 64 bit system"}, "Yes, treat it as a 64 bit system");
                if(whichBitDepth<0||whichBitDepth>1){
                    System.exit(0);
                }
            }
            if(whichBitDepth==BIT_32){
                current++;
            }
            String[] osPaths = nativesPaths[whichOS];
            //32 bit
            if(!new File(getLibraryRoot()+"/natives32.zip").exists()){
                downloadSize += 303;
            }
            if((!new File(getLibraryRoot()+"/natives64.zip").exists())&&whichBitDepth==BIT_64){
                downloadSize += 338;
            }
            addRequiredLibrary("https://dl.dropboxusercontent.com/s/p7v72lix4gl96co/lwjgl.jar?dl=1&token_hash=AAG5TMAYw0Oq1_xwgVjKoE8FkKXMaWOfpj5cau1UuWKZlA", "lwjgl.jar", 912);
            addRequiredLibrary("https://dl.dropboxusercontent.com/s/9ylaq5w5vzj1lgi/jinput.jar?dl=1&token_hash=AAHILxU3uc-UU5vXj7N4i5s1huBKYSzKGgKq3MawNJB05w", "jinput.jar", 210);
            addRequiredLibrary("https://dl.dropboxusercontent.com/s/fog6w5pcxqf4zd9/lwjgl_util.jar?dl=1&token_hash=AAHwYq0uL4zeuTrLoi8EiG_RiUeMDZDsnlm4KYNScpy0Sw", "lwjgl_util.jar", 170);
            addRequiredLibrary("https://dl.dropboxusercontent.com/s/60en1x8in11leqn/lzma.jar?dl=1&token_hash=AAGUFJwmD9jKmk7j4M53Xr0_6Sisf5RSRW3JAjRgsml4gg", "lzma.jar", 6);
            for(String[] lib : requiredLibraries.keySet()){
                if(!new File(getLibraryRoot()+"/"+lib[1]).exists()){
                    downloadSize+=requiredLibraries.get(lib);
                }
            }
            if(!new File(getLibraryRoot()+"/Simplelibrary "+requiredSimpleLibraryVersion+".jar").exists()){
                downloadSize+=simplelibrarySize+2;//2 kb for the versions file
            }
            if(requiredSimpleLibraryExtendedVersion!=null&&(!new File(getLibraryRoot()+"/Simplelibrary_extended "+requiredSimpleLibraryExtendedVersion+".jar").exists())){
                downloadSize+=simplelibraryExtendedSize+1;//1 kb for the versions file
            }
            if(downloadSize>0&&!allowDownload){
                if(JOptionPane.showConfirmDialog(null, applicationName+" has a few dependencies that must be downloaded before play.\nThere is up to about "+(downloadSize>=1000?(downloadSize/1000+"MB"):(downloadSize+" KB"))+" to download.\nDownload them now?", applicationName+" - Dependencies", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
                        !=JOptionPane.YES_OPTION){
                    //no download
                    JOptionPane.showMessageDialog(null, applicationName+" will now exit.", "Exit", JOptionPane.OK_OPTION);
                    System.exit(0);
                }
            }
            allowDownload = true;
            total = 8+requiredLibraries.size();
            frame = new JFrame("Download Progress");
            bar = new JProgressBar(0, total);
            frame.add(bar);
            frame.setSize(300, 70);
            bar.setBounds(0, 0, 300, 70);
            if(downloadSize>0){
                frame.setVisible(true);
            }
            File bit32 = downloadFile(osPaths[BIT_32], new File(getLibraryRoot()+"/natives32.zip"));
            File bit64 = whichBitDepth==BIT_64?downloadFile(osPaths[BIT_64], new File(getLibraryRoot()+"/natives64.zip")):null;
            File nativesDir = new File(getLibraryRoot()+"/natives");
            if(bit32==null||(whichBitDepth==BIT_64&&bit64==null&&osPaths[BIT_64]!=null)){
                JOptionPane.showMessageDialog(null, "Could not download the required natives!  "+applicationName+" will now exit.", "Native Download Failed", JOptionPane.OK_OPTION);
                System.exit(0);
            }
            extractFile(bit32, nativesDir);
            if(bit64!=null){
                extractFile(bit64, nativesDir);
            }
            File simplibVersions = forceDownloadFile("https://www.dropbox.com/s/as5y1ik7gb8gp6k/versions.dat?dl=1", new File("simplib.versions"));
            if(simplibVersions==null){
                System.err.println("Failed to download Simplelibrary versions file! Skipping...");
                addRequiredLibrary(null, "Simplelibrary "+requiredSimpleLibraryVersion+".jar", simplelibrarySize);
            }else{
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(simplibVersions)));
                ArrayList<String> versions = new ArrayList<>();
                HashMap<String, String> simplib = new HashMap<>();
                String line;
                while((line = reader.readLine())!=null){
                    if(line.isEmpty())continue;
                    versions.add(line.split("=", 2)[0]);
                    simplib.put(line.split("=", 2)[0], line.split("=", 2)[1]);
                }
                reader.close();
                if(!versions.contains(requiredSimpleLibraryVersion)){
                    System.err.println("Unknown simplelibrary version "+requiredSimpleLibraryVersion+"! Downloading latest version");
                    requiredSimpleLibraryVersion = versions.get(versions.size()-1);
                }
                addRequiredLibrary(simplib.get(requiredSimpleLibraryVersion), "Simplelibrary "+requiredSimpleLibraryVersion+".jar", simplelibrarySize);
                simplibVersions.delete();
            }
            if(requiredSimpleLibraryExtendedVersion!=null){
                File simpLibExtendedVersions = forceDownloadFile("https://www.dropbox.com/s/7k4ri81to8hc9n2/versions.dat?dl=1", new File("simplibextended.versions"));
                if(simpLibExtendedVersions==null){
                    System.err.println("Failed to download Simplelibrary Extended versions file! Skipping...");
                    addRequiredLibrary(null, "Simplelibrary_extended "+requiredSimpleLibraryExtendedVersion+".jar", simplelibraryExtendedSize);
                }else{
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(simpLibExtendedVersions)));
                    ArrayList<String> versions = new ArrayList<>();
                    HashMap<String, String> simpLibExtended = new HashMap<>();
                    String line;
                    while((line = reader.readLine())!=null){
                        if(line.isEmpty())continue;
                        versions.add(line.split("=", 2)[0]);
                        simpLibExtended.put(line.split("=", 2)[0], line.split("=", 2)[1]);
                    }
                    reader.close();
                    if(!versions.contains(requiredSimpleLibraryExtendedVersion)){
                        System.err.println("Unknown Simplelibrary_extended version "+requiredSimpleLibraryExtendedVersion+"! Downloading latest version");
                        requiredSimpleLibraryExtendedVersion = versions.get(versions.size()-1);
                    }
                    addRequiredLibrary(simpLibExtended.get(requiredSimpleLibraryExtendedVersion), "Simplelibrary_extended "+requiredSimpleLibraryExtendedVersion+".jar", simplelibraryExtendedSize);
                    simpLibExtendedVersions.delete();
                }
            }
            File[] requiredLibs = new File[requiredLibraries.size()];
            int n = 0;
            for(String[] lib : requiredLibraries.keySet()){
                String url = lib[0];
                String filename = lib[1];
                requiredLibs[n] = downloadFile(url, new File(getLibraryRoot()+"/"+filename));
                n++;
            }
            frame.dispose();
            String[] additionalClasspathElements = new String[requiredLibs.length+4];
            for(int i = 0; i<requiredLibs.length; i++){
                if(requiredLibs[i]==null){
                    JOptionPane.showMessageDialog(null, "Failed to download dependencies!\n"+applicationName+" will now exit.", "Exit", JOptionPane.OK_OPTION);
                    System.exit(0);
                }
                additionalClasspathElements[i] = requiredLibs[i].getAbsolutePath();
            }
            System.out.println("Loading...");
            theargs.add(0, "Skip Dependencies");
            final Process p = restart(new String[]{"-Djava.library.path="+nativesDir.getAbsolutePath()}, theargs.toArray(new String[theargs.size()]), additionalClasspathElements, Main.class);
            final int[] finished = {0};
            new Thread("System.out transfer"){
                public void run(){
                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    try{
                        while((line=in.readLine())!=null){
                            System.out.println(line);
                        }
                    }catch(IOException ex){
                        throw new RuntimeException(ex);
                    }
                    finished[0]++;
                    if(finished[0]>1){
                        System.exit(0);
                    }
                }
            }.start();
            new Thread("System.err transfer"){
                public void run(){
                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String line;
                    try{
                        while((line=in.readLine())!=null){
                            System.err.println(line);
                        }
                    }catch(IOException ex){
                        throw new RuntimeException(ex);
                    }
                    finished[0]++;
                    if(finished[0]>1){
                        System.exit(0);
                    }
                }
            }.start();
            Thread inTransfer = new Thread("System.in transfer"){
                public void run(){
                    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                    PrintWriter out = new PrintWriter(p.getOutputStream());
                    String line;
                    try{
                        while((line=in.readLine())!=null){
                            out.println(line);
                            out.flush();
                        }
                    }catch(IOException ex){
                        throw new RuntimeException(ex);
                    }
                }
            };
            inTransfer.setDaemon(true);
            inTransfer.start();
            return null;
        }
        theargs.remove(0);
        return theargs.toArray(new String[theargs.size()]);
    }
    private static File downloadFile(String link, File destinationFile){
        current++;
        bar.setValue(current);
        if(destinationFile.exists()||link==null){
            return destinationFile;
        }
        if(!allowDownload){
            System.err.println("Failed to download file!\nDownload has not been allowed!");
            return null;
        }
        if(destinationFile.getParentFile()!=null)destinationFile.getParentFile().mkdirs();
        try {
            URL url = new URL(link);
            int fileSize;
            URLConnection connection = url.openConnection();
            connection.setDefaultUseCaches(false);
            if ((connection instanceof HttpURLConnection)) {
                ((HttpURLConnection)connection).setRequestMethod("HEAD");
                int code = ((HttpURLConnection)connection).getResponseCode();
                if (code / 100 == 3) {
                    return null;
                }
            }
            fileSize = connection.getContentLength();
            byte[] buffer = new byte[65535];
            int unsuccessfulAttempts = 0;
            int maxUnsuccessfulAttempts = 3;
            boolean downloadFile = true;
            while (downloadFile) {
                downloadFile = false;
                URLConnection urlconnection = url.openConnection();
                if ((urlconnection instanceof HttpURLConnection)) {
                    urlconnection.setRequestProperty("Cache-Control", "no-cache");
                    urlconnection.connect();
                }
                String targetFile = destinationFile.getName();
                FileOutputStream fos;
                int downloadedFileSize;
                try (InputStream inputstream=getRemoteInputStream(targetFile, urlconnection)) {
                    fos=new FileOutputStream(destinationFile);
                    downloadedFileSize=0;
                    int read;
                    while ((read = inputstream.read(buffer)) != -1) {
                        fos.write(buffer, 0, read);
                        downloadedFileSize += read;
                    }
                }
                fos.close();
                if (((urlconnection instanceof HttpURLConnection)) && 
                    ((downloadedFileSize != fileSize) && (fileSize > 0))){
                    unsuccessfulAttempts++;
                    if (unsuccessfulAttempts < maxUnsuccessfulAttempts){
                        downloadFile = true;
                    }else{
                        throw new Exception("failed to download "+targetFile);
                    }
                }
            }
            return destinationFile;
        }catch (Exception ex){
            return null;
        }
    }
    private static File forceDownloadFile(String link, File destinationFile){
        if(destinationFile.exists())destinationFile.delete();
        return downloadFile(link, destinationFile);
    }
    public static InputStream getRemoteInputStream(String currentFile, final URLConnection urlconnection) throws Exception {
        final InputStream[] is = new InputStream[1];
        for (int j = 0; (j < 3) && (is[0] == null); j++) {
            Thread t = new Thread() {
                public void run() {
                    try {
                        is[0] = urlconnection.getInputStream();
                    }catch (IOException localIOException){}
                }
            };
            t.setName("FileDownloadStreamThread");
            t.start();
            int iterationCount = 0;
            while ((is[0] == null) && (iterationCount++ < 5)){
                try {
                    t.join(1000L);
                } catch (InterruptedException localInterruptedException) {
                }
            }
            if (is[0] != null){
                continue;
            }
            try {
                t.interrupt();
                t.join();
            } catch (InterruptedException localInterruptedException1) {
            }
        }
        if (is[0] == null) {
            throw new Exception("Unable to download "+currentFile);
        }
        return is[0];
    }
    private static void extractFile(File fromZip, File toDir){
        if(!fromZip.exists()){
            return;
        }
        toDir.mkdirs();
        try(ZipInputStream in = new ZipInputStream(new FileInputStream(fromZip))){
            ZipEntry entry;
            while((entry = in.getNextEntry())!=null){
                File destFile = new File(toDir.getAbsolutePath()+"/"+entry.getName());
                if(destFile.exists())continue;
                delete(destFile);
                try(FileOutputStream out = new FileOutputStream(destFile)){
                    byte[] buffer = new byte[1024];
                    int read = 0;
                    while((read=in.read(buffer))>=0){
                        out.write(buffer, 0, read);
                    }
                }
            }
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    private static void delete(File file){
        if(!file.exists()){
            return;
        }
        if(file.isDirectory()){
            File[] files = file.listFiles();
            if(files!=null){
                for(File afile : files){
                    delete(afile);
                }
            }
            file.delete();
        }else{
            file.delete();
        }
    }
    /**
     * Restarts the program.  This method will return normally if the program was properly restarted or throw an exception if it could not be restarted.
     * @param vmArgs The VM arguments for the new instance
     * @param applicationArgs The application arguments for the new instance
     * @param additionalFiles Any additional files to include in the classpath
     * @param mainClass The program's main class.  The new instance is started with this as the specified main class.
     * @throws URISyntaxException if a URI cannot be created to obtain the filepath to the jarfile
     * @throws IOException if Java is not in the PATH environment variable
     */
    public static Process restart(String[] vmArgs, String[] applicationArgs, String[] additionalFiles, Class<?> mainClass) throws URISyntaxException, IOException{
        ArrayList<String> params = new ArrayList<>();
        params.add("java");
        params.addAll(Arrays.asList(vmArgs));
        params.add("-classpath");
        String filepath = mainClass.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        for(String str : additionalFiles){
            filepath+=(os==OS_LINUX?":":";")+str;
        }
        params.add(filepath);
        params.add(mainClass.getName());
        params.addAll(Arrays.asList(applicationArgs));
        ProcessBuilder builder = new ProcessBuilder(params);
        return builder.start();
    }
    /**
     * Starts the requested Java application the program.  This method will return normally if the program was properly restarted or throw an exception if it could not be restarted.
     * @param vmArgs The VM arguments for the new instance
     * @param applicationArgs The application arguments for the new instance
     * @param file The program file.  The new instance is started with this as the specified main class.
     * @throws URISyntaxException if a URI cannot be created to obtain the filepath to the jarfile
     * @throws IOException if Java is not in the PATH environment variable
     */
    public static Process startJava(String[] vmArgs, String[] applicationArgs, File file) throws URISyntaxException, IOException{
        ArrayList<String> params = new ArrayList<>();
        params.add("java");
        params.addAll(Arrays.asList(vmArgs));
        params.add("-jar");
        params.add(file.getAbsolutePath());
        params.addAll(Arrays.asList(applicationArgs));
        ProcessBuilder builder = new ProcessBuilder(params);
        return builder.start();
    }
}
package planner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
public class Updater{
    private ArrayList<String> versions = new ArrayList<>();
    private HashMap<String, String> links = new HashMap<>();
    private int versionsBehind;
    private String currentVersion;
    private String applicationName;
    public static Updater read(File file, String currentVersion, String applicationName){
        if(!file.exists()||!file.isFile()){
            return null;
        }
        Updater updater = new Updater();
        try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
            String line;
            while((line = in.readLine())!=null){
                String[] spl = line.split("=");
                if(spl.length!=2){
                    continue;
                }
                updater.versions.add(spl[0]);
                updater.links.put(spl[0], spl[1]);
            }
        }catch(IOException ex){}
        updater.setCurrentVersion(currentVersion);
        updater.applicationName = applicationName;
        return updater;
    }
    public static Updater read(String fileURL, String currentVersion, String applicationName){
        Updater updater = new Updater();
        File file = new File("version.version");
        file.delete();
        downloadFile(fileURL, file);
        try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
            String line;
            while((line = in.readLine())!=null){
                String[] spl = line.split("=", 2);
                if(spl.length!=2){
                    continue;
                }
                updater.versions.add(spl[0]);
                updater.links.put(spl[0], spl[1]);
            }
        }catch(IOException ex){}
        updater.setCurrentVersion(currentVersion);
        updater.applicationName = applicationName;
        return updater;
    }
    private static File downloadFile(String link, File destinationFile){
        if(destinationFile.exists()||link==null){
            return destinationFile;
        }
        destinationFile.getParentFile().mkdirs();
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
    private void setCurrentVersion(String currentVersion){
        this.currentVersion = currentVersion;
        versionsBehind = findVersionsBehind(currentVersion);
    }
    public int findVersionsBehind(String version){
        int index = versions.indexOf(version);
        if(index<0){
            return -1;
        }else{
            return versions.size()-1-index;
        }
    }
    public int getVersionsBehindLatestDownloadable(){
        return versionsBehind-findVersionsBehind(getLatestDownloadableVersion());
    }
    public String getLatestDownloadableVersion(){
        for(int i = versions.size()-1; i>=0; i--){
            if(links.get(versions.get(i))!=null&&!links.get(versions.get(i)).isEmpty()){
                return versions.get(i);
            }
        }
        return null;
    }
    public File update(String version) throws URISyntaxException{
        String link = links.get(version);
        if(link==null){
            return null;
        }
        String fileName = applicationName+" "+currentVersion+".jar";
        String newFilename = applicationName+" "+version+".jar";
        String existingFilename = new File(Updater.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getName();
        String temporaryFilename = newFilename;
        for(int i = 2; new File(temporaryFilename).exists(); i++){
            temporaryFilename = applicationName+" "+version+" ("+i+").jar";
        }
        //<editor-fold defaultstate="collapsed" desc="Downloading">
        try {
            URL[] urlList = {new URL(link)};
            int[] fileSizes = new int[urlList.length];
            boolean[] skip = new boolean[urlList.length];
            for (int j = 0; j < urlList.length; j++) {
                URLConnection connection = urlList[j].openConnection();
                connection.setDefaultUseCaches(false);
                skip[j] = false;
                if ((connection instanceof HttpURLConnection)) {
                    ((HttpURLConnection)connection).setRequestMethod("HEAD");
                    int code = ((HttpURLConnection)connection).getResponseCode();
                    if (code / 100 == 3) {
                        skip[j] = true;
                    }
                }
                fileSizes[j] = connection.getContentLength();
            }
            byte[] buffer = new byte[65536];
            for (int j = 0; j < urlList.length; j++) {
                int unsuccessfulAttempts = 0;
                int maxUnsuccessfulAttempts = 3;
                boolean downloadFile = true;
                while (downloadFile) {
                    downloadFile = false;
                    URLConnection urlconnection = urlList[j].openConnection();
                    if ((urlconnection instanceof HttpURLConnection)) {
                        urlconnection.setRequestProperty("Cache-Control", "no-cache");
                        urlconnection.connect();
                    }
                    String targetFile = temporaryFilename;
                    InputStream inputstream = getRemoteInputStream(targetFile, urlconnection);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int fileSize = 0;
                    MessageDigest m = MessageDigest.getInstance("MD5");
                    int bufferSize;
                    while ((bufferSize = inputstream.read(buffer, 0, buffer.length)) != -1) {
                        fos.write(buffer, 0, bufferSize);
                        m.update(buffer, 0, bufferSize);
                        fileSize += bufferSize;
                    }
                    inputstream.close();
                    fos.close();
                    if ((fileSize != fileSizes[j]) && (fileSizes[j] > 0)){
                        unsuccessfulAttempts++;
                        if (unsuccessfulAttempts < maxUnsuccessfulAttempts){
                            downloadFile = true;
                        }else{
                            throw new Exception("failed to download "+targetFile);
                        }
                    }
                }
            }
        }catch (Exception ex){
            Sys.error(ErrorLevel.severe, null, ex, ErrorCategory.InternetIO);
            new File(temporaryFilename).delete();
        }//</editor-fold>
        if((!existingFilename.equals(fileName))&&false){
            boolean useExisting = JOptionPane.showConfirmDialog(null, "Would you like to use the same file for the new version of "+applicationName+"?", applicationName+" Update", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION;
            if(useExisting){
                File file = new File(Updater.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                File otherFile = new File(temporaryFilename);
                try{
                    file.delete();
                    otherFile.renameTo(file);
                    return file;
                }catch(Throwable twbl){
                    JOptionPane.showMessageDialog(null, "Could not replace file!");
                }
                return new File(temporaryFilename);
            }
        }
        newFilename = temporaryFilename;
        return new File(temporaryFilename);
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
}

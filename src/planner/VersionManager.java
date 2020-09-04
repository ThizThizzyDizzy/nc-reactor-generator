package planner;
import java.util.ArrayList;
/**
 * A version manager class, created to make version management easy.
 * @author Bryan
 */
public class VersionManager{
    /**
     * The current version (This version)
     * @since Public Acceptance Text 1 (Initial Release)
     */
    public static final String currentVersion;
    /**
     * List of all recognized versions.
     * @since Public Acceptance Text 1 (Initial Release)
     */
    private static final ArrayList<String> versions = new ArrayList<>();
    /**
     * The earliest version that the current version has back compatibility for.
     * @since Public Acceptance Text 1 (Initial Release)
     */
    private static String backCompatibleTo;
    static{
        addVersion("2.0");
        addVersion("2.0.1");
        addVersion("2.0.2");
        addVersion("2.0.3");
        addVersion("2.1.0");
        addVersion("2.2.0");
        addVersion("2.3.0");
        addVersion("2.3.1");
        addVersion("2.3.2");
        addVersion("2.3.3");
        addVersion("2.3.4");
        addVersion("2.3.5");
        addVersion("2.4.0");
        addVersion("2.4.1");
        addVersion("2.5.0");
        addVersion("2.5.1");
        addVersion("2.6.0");
        addVersion("2.7.0");
        addVersion("2.7.1");
        addVersion("2.8.0");
        addVersion("2.8.1");
        addVersion("2.8.2");
        addVersion("2.9.0");
        addVersion("2.9.1");
        addVersion("2.9.2");
        addVersion("2.9.3");
        addVersion("2.9.4");
        addVersion("2.10.0");
        addVersion("2.10.1");
        addVersion("2.10.2");
        addVersion("2.10.3");
        addVersion("2.10.4");
        addVersion("2.10.5");
        addVersion("2.11");
        addVersion("2.11.1");
        addVersion("2.11.2");
        addVersion("2.11.3");
        addVersion("2.11.4");
        currentVersion = versions.get(versions.size()-1);
    }
    /**
     * Adds a version to the versions list.  Used only in the static initializer of this class.
     * @param string The name of the version to add
     * @since Public Acceptance Text 1 (Initial Release)
     */
    private static void addVersion(String string){
        if(versions.contains(string)){
            throw new IllegalArgumentException("Cannot add same version twice!");
        }
        versions.add(string);
        if(breakBackCompatability){
            breakBackCompatability = false;
            backCompatibleTo = getVersion(versions.size()-1);
        }
    }
    /**
     * Gets the version ID for the specified String version
     * @param version The version to ID
     * @return The version ID (-1 if <code>version</code> is not a valid version)
     * @since Public Acceptance Text 1 (Initial Release)
     */
    public static int getVersionID(String version){
        return versions.indexOf(version);
    }
    /**
     * Gets the String version for the specified version ID
     * @param ID the version ID
     * @return The String version
     * @throws IndexOutOfBoundsException if the ID is not a valid version ID
     * @since Public Acceptance Text 1 (Initial Release)
     */
    public static String getVersion(int ID){
        return versions.get(ID);
    }
    private static boolean breakBackCompatability;
    /**
     * Informs the version manager that there is no back compatibility.
     * Sets <code>backCompatibleTo</code> to the next index in <code>versions</code>
     * Used only in the static initializer of this class.
     * @since Public Acceptance Text 1 (Initial Release)
     */
    private static void breakBackCompatability(){
        breakBackCompatability = true;
    }
    /**
     * Checks if the system has back compatibility for the specified version ID
     * @param versionID The version ID to check
     * @return If back compatibility exists for the specified version ID
     * @since Public Acceptance Text 1 (Initial Release)
     */
    public static boolean isCompatible(int versionID){
        if(backCompatibleTo==null){
            breakBackCompatability();
        }
        return getVersionID(backCompatibleTo)<=versionID;
    }
    public static boolean isCompatible(String version){
        return isCompatible(getVersionID(version));
    }
}

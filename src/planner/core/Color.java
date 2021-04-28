package planner.core;
public class Color{
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color PINK = new Color(255, 175, 175);
    public static final Color GRAY = new Color(128, 128, 128);
    public static Color fromAWT(java.awt.Color color){
        return new Color(color.getRGB());
    }
    private final int r;
    private final int g;
    private final int b;
    private final int a;
    public Color(int r, int g, int b){
        this(r, g, b, 255);
    }
    public Color(int r, int g, int b, int a){
        if(r<0||r>255)throw new IllegalArgumentException("Red component must be within 0-255!");
        if(g<0||g>255)throw new IllegalArgumentException("Green component must be within 0-255!");
        if(b<0||b>255)throw new IllegalArgumentException("Blue component must be within 0-255!");
        if(a<0||a>255)throw new IllegalArgumentException("Alpha component must be within 0-255!");
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
    public Color(float r, float g, float b){
        this((int)(r*255),(int)(g*255),(int)(b*255));
    }
    public Color(float r, float g, float b, float a){
        this((int)(r*255),(int)(g*255),(int)(b*255), (int)(a*255));
    }
    public Color(int argb){
        this(rgbToRed(argb), rgbToGreen(argb), rgbToBlue(argb), rgbToAlpha(argb));
    }
    public int getRed(){
        return r;
    }
    public int getGreen(){
        return g;
    }
    public int getBlue(){
        return b;
    }
    public int getAlpha(){
        return a;
    }
    public int getRGB(){
        return  ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                 (b & 0xFF);
    }
    public static int rgbToRed(int argb){
        return (argb>>16)&0xFF;
    }
    public static int rgbToGreen(int argb){
        return (argb>>8)&0xFF;
    }
    public static int rgbToBlue(int argb){
        return argb&0xFF;
    }
    public static int rgbToAlpha(int argb){
        return (argb>>24)&0xff;
    }
    public java.awt.Color toAWT(){
        return new java.awt.Color(r, g, b, a);
    }
    //stolen straight from AWT
    public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
        float hue, saturation, brightness;
        if (hsbvals == null) {
            hsbvals = new float[3];
        }
        int cmax = (r > g) ? r : g;
        if (b > cmax) cmax = b;
        int cmin = (r < g) ? r : g;
        if (b < cmin) cmin = b;

        brightness = ((float) cmax) / 255.0f;
        if (cmax != 0)
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        else
            saturation = 0;
        if (saturation == 0)
            hue = 0;
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax)
                hue = bluec - greenc;
            else if (g == cmax)
                hue = 2.0f + redc - bluec;
            else
                hue = 4.0f + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0)
                hue = hue + 1.0f;
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }
    //stolen straight from AWT
    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float)Math.floor(hue)) * 6.0f;
            float f = h - (float)java.lang.Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
            case 0:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (t * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 1:
                r = (int) (q * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 2:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (t * 255.0f + 0.5f);
                break;
            case 3:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (q * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 4:
                r = (int) (t * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 5:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (q * 255.0f + 0.5f);
                break;
            }
        }
        return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
    }
}
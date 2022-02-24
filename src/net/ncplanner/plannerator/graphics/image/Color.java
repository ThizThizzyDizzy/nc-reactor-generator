package net.ncplanner.plannerator.graphics.image;
public class Color{
    public final static Color WHITE      = new Color(255, 255, 255);
    public final static Color LIGHT_GRAY = new Color(192, 192, 192);
    public final static Color GRAY       = new Color(128, 128, 128);
    public final static Color DARK_GRAY  = new Color(64, 64, 64);
    public final static Color BLACK      = new Color(0, 0, 0);
    public final static Color RED        = new Color(255, 0, 0);
    public final static Color PINK       = new Color(255, 175, 175);
    public final static Color ORANGE     = new Color(255, 200, 0);
    public final static Color YELLOW     = new Color(255, 255, 0);
    public final static Color GREEN      = new Color(0, 255, 0);
    public final static Color MAGENTA    = new Color(255, 0, 255);
    public final static Color CYAN       = new Color(0, 255, 255);
    public final static Color BLUE       = new Color(0, 0, 255);
    private int r;
    private int g;
    private int b;
    private int a;
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
        this(getRed(argb), getGreen(argb), getBlue(argb), getAlpha(argb));
    }
    public Color(int rgb, int alpha){
        this(getRed(rgb), getGreen(rgb), getBlue(rgb), alpha);
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
    public static int getRGB(byte r, byte g, byte b, byte a){
        return  ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                 (b & 0xFF);
    }
    public float getHue(){
        return getHSB()[0];
    }
    public float getSaturation(){
        return getHSB()[1];
    }
    public float getBrightness(){
        return getHSB()[2];
    }
    public float[] getHSB(){
        float hue, saturation, brightness;
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
        return new float[]{hue,saturation,brightness};
    }
    public static Color fromHSB(float hue, float saturation, float brightness){
        return fromHSB(hue, saturation, brightness, 255);
    }
    public static Color fromHSB(float hue, float saturation, float brightness, int alpha){
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
        return new Color(0xff000000 | (r << 16) | (g << 8) | (b << 0), alpha);
    }
    public static int getRed(int argb){
        return (argb>>16)&0xFF;
    }
    public static int getGreen(int argb){
        return (argb>>8)&0xFF;
    }
    public static int getBlue(int argb){
        return argb&0xFF;
    }
    public static int getAlpha(int argb){
        return (argb>>24)&0xff;
    }
}
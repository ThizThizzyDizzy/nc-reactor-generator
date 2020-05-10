package underhaul;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
public class ReactorPanel extends JPanel{
    private final Reactor reactor;
    public ReactorPanel(Reactor reactor){
        this.reactor = reactor;
    }
    @Override
    protected void paintComponent(Graphics g) {
        draw(g, getWidth(), getHeight());
    }
    protected void draw(Graphics g, int w, int h) {
        g.setColor(new Color(240,240,240));
        g.fillRect(0, 0, w, h);
        if(reactor==null)return;
        BufferedImage image = reactor.getImage();
        int size = Math.min(getWidth()/image.getWidth(), getHeight()/image.getHeight());
        g.drawImage(reactor.getImage(), 0, 0, image.getWidth()*size, image.getHeight()*size, null);
    }
}
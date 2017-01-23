

import java.awt.*;
import java.awt.geom.*;
import static java.awt.Color.*;
import static java.awt.MultipleGradientPaint.CycleMethod.*;
import static java.awt.MultipleGradientPaint.ColorSpaceType.*;

/**
 * This class has been automatically generated using
 * <a href="https://flamingo.dev.java.net">Flamingo SVG transcoder</a>.
 */
public class Laser {

    /**
     * Paints the transcoded SVG image on the specified graphics context. You
     * can install a custom transformation on the graphics context to scale the
     * image.
     * 
     * @param g Graphics context.
     */
    public static void paint(Graphics2D g) {
        Shape shape = null;
        
        float origAlpha = 1.0f;
        Composite origComposite = ((Graphics2D)g).getComposite();
        if (origComposite instanceof AlphaComposite) {
            AlphaComposite origAlphaComposite = (AlphaComposite)origComposite;
            if (origAlphaComposite.getRule() == AlphaComposite.SRC_OVER) {
                origAlpha = origAlphaComposite.getAlpha();
            }
        }
        
        java.util.LinkedList<AffineTransform> transformations = new java.util.LinkedList<AffineTransform>();
        

        // 

        // _0

        // _0_0
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(683.48, 1007.247);
        ((GeneralPath) shape).curveTo(683.48, 1013.875, 678.107, 1019.247, 671.48, 1019.247);
        ((GeneralPath) shape).lineTo(405.87, 1019.247);
        ((GeneralPath) shape).curveTo(399.24298, 1019.247, 393.87, 1013.875, 393.87, 1007.247);
        ((GeneralPath) shape).lineTo(393.87, 472.805);
        ((GeneralPath) shape).curveTo(393.87, 466.17798, 399.24298, 460.805, 405.87, 460.805);
        ((GeneralPath) shape).lineTo(671.48, 460.805);
        ((GeneralPath) shape).curveTo(678.107, 460.805, 683.48, 466.17798, 683.48, 472.805);
        ((GeneralPath) shape).lineTo(683.48, 1007.247);
        ((GeneralPath) shape).closePath();

        g.setPaint(new Color(0x402230));
        g.fill(shape);
        g.setPaint(BLACK);
        g.setStroke(new BasicStroke(1, 0, 0, 10));
        g.draw(shape);

        // _0_1
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(504.113, 496.239);
        ((GeneralPath) shape).lineTo(469.551, 436.376);
        ((GeneralPath) shape).lineTo(504.113, 376.513);
        ((GeneralPath) shape).lineTo(573.237, 376.513);
        ((GeneralPath) shape).lineTo(607.8, 436.376);
        ((GeneralPath) shape).lineTo(573.237, 496.239);
        ((GeneralPath) shape).closePath();

        g.setPaint(new Color(0x1B1528));
        g.fill(shape);
        g.setPaint(BLACK);
        g.draw(shape);

        // _0_2
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(529.088, 424.165);
        ((GeneralPath) shape).lineTo(519.5, 407.559);
        ((GeneralPath) shape).lineTo(529.088, 390.952);
        ((GeneralPath) shape).lineTo(548.263, 390.952);
        ((GeneralPath) shape).lineTo(557.851, 407.559);
        ((GeneralPath) shape).lineTo(548.263, 424.165);
        ((GeneralPath) shape).closePath();

        g.setPaint(new Color(0x68262A));
        g.fill(shape);
        g.setPaint(BLACK);
        g.draw(shape);

    }

    /**
     * Returns the X of the bounding box of the original SVG image.
     * 
     * @return The X of the bounding box of the original SVG image.
     */
    public static int getOrigX() {
        return 394;
    }

    /**
     * Returns the Y of the bounding box of the original SVG image.
     * 
     * @return The Y of the bounding box of the original SVG image.
     */
    public static int getOrigY() {
        return 377;
    }

    /**
     * Returns the width of the bounding box of the original SVG image.
     * 
     * @return The width of the bounding box of the original SVG image.
     */
    public static int getOrigWidth() {
        return 291;
    }

    /**
     * Returns the height of the bounding box of the original SVG image.
     * 
     * @return The height of the bounding box of the original SVG image.
     */
    public static int getOrigHeight() {
        return 644;
    }
}


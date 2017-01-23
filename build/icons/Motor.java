

import java.awt.*;
import java.awt.geom.*;
import static java.awt.Color.*;
import static java.awt.MultipleGradientPaint.CycleMethod.*;
import static java.awt.MultipleGradientPaint.ColorSpaceType.*;

/**
 * This class has been automatically generated using
 * <a href="https://flamingo.dev.java.net">Flamingo SVG transcoder</a>.
 */
public class Motor {

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

        // _0_0_0
        shape = new Ellipse2D.Double(10, 10, 80, 80);
        g.setPaint(new RadialGradientPaint(new Point2D.Double(0.5, 0.5), 0.5f, new Point2D.Double(0.5, 0.5), new float[]{0, 1}, new Color[]{new Color(0xFEBCBCBC, true), new Color(0xFDAFAFAF, true)}, NO_CYCLE, SRGB, new AffineTransform(80, 0, 0, 80, 10, 10)));
        g.fill(shape);
        g.setPaint(BLACK);
        g.setStroke(new BasicStroke(2, 0, 0, 4));
        g.draw(shape);
        transformations.offer(g.getTransform());
        g.transform(new AffineTransform(2.4577007f, 0, 0, 2.4577007f, -39.061825f, -47.80803f));

        // _0_0_1
        // M
        shape = new GeneralPath();
        ((GeneralPath) shape).moveTo(36.964428, 41.85906);
        ((GeneralPath) shape).lineTo(35.839428, 41.85906);
        ((GeneralPath) shape).lineTo(32.058178, 33.249683);
        ((GeneralPath) shape).lineTo(31.870678, 33.249683);
        ((GeneralPath) shape).lineTo(31.870678, 44.99187);
        ((GeneralPath) shape).lineTo(33.683178, 44.99187);
        ((GeneralPath) shape).quadTo(34.03474, 44.99187, 34.187084, 45.124683);
        ((GeneralPath) shape).quadTo(34.339428, 45.257496, 34.339428, 45.48406);
        ((GeneralPath) shape).quadTo(34.339428, 45.694996, 34.187084, 45.83562);
        ((GeneralPath) shape).quadTo(34.03474, 45.976246, 33.683178, 45.976246);
        ((GeneralPath) shape).lineTo(30.050365, 45.976246);
        ((GeneralPath) shape).quadTo(29.698803, 45.976246, 29.54646, 45.83562);
        ((GeneralPath) shape).quadTo(29.394115, 45.694996, 29.394115, 45.48406);
        ((GeneralPath) shape).quadTo(29.394115, 45.257496, 29.54646, 45.124683);
        ((GeneralPath) shape).quadTo(29.698803, 44.99187, 30.050365, 44.99187);
        ((GeneralPath) shape).lineTo(30.886303, 44.99187);
        ((GeneralPath) shape).lineTo(30.886303, 33.249683);
        ((GeneralPath) shape).lineTo(30.261303, 33.249683);
        ((GeneralPath) shape).quadTo(29.90974, 33.249683, 29.757397, 33.112965);
        ((GeneralPath) shape).quadTo(29.605053, 32.976246, 29.605053, 32.757496);
        ((GeneralPath) shape).quadTo(29.605053, 32.530933, 29.757397, 32.39812);
        ((GeneralPath) shape).quadTo(29.90974, 32.26531, 30.261303, 32.26531);
        ((GeneralPath) shape).lineTo(32.675365, 32.26531);
        ((GeneralPath) shape).lineTo(36.401928, 40.749683);
        ((GeneralPath) shape).lineTo(40.073803, 32.26531);
        ((GeneralPath) shape).lineTo(42.487865, 32.26531);
        ((GeneralPath) shape).quadTo(42.84724, 32.26531, 42.999584, 32.39812);
        ((GeneralPath) shape).quadTo(43.151928, 32.530933, 43.151928, 32.757496);
        ((GeneralPath) shape).quadTo(43.151928, 32.976246, 42.999584, 33.112965);
        ((GeneralPath) shape).quadTo(42.84724, 33.249683, 42.487865, 33.249683);
        ((GeneralPath) shape).lineTo(41.87849, 33.249683);
        ((GeneralPath) shape).lineTo(41.87849, 44.99187);
        ((GeneralPath) shape).lineTo(42.698803, 44.99187);
        ((GeneralPath) shape).quadTo(43.058178, 44.99187, 43.21052, 45.124683);
        ((GeneralPath) shape).quadTo(43.362865, 45.257496, 43.362865, 45.48406);
        ((GeneralPath) shape).quadTo(43.362865, 45.694996, 43.21052, 45.83562);
        ((GeneralPath) shape).quadTo(43.058178, 45.976246, 42.698803, 45.976246);
        ((GeneralPath) shape).lineTo(39.073803, 45.976246);
        ((GeneralPath) shape).quadTo(38.72224, 45.976246, 38.56599, 45.83562);
        ((GeneralPath) shape).quadTo(38.40974, 45.694996, 38.40974, 45.48406);
        ((GeneralPath) shape).quadTo(38.40974, 45.257496, 38.562084, 45.124683);
        ((GeneralPath) shape).quadTo(38.714428, 44.99187, 39.073803, 44.99187);
        ((GeneralPath) shape).lineTo(40.894115, 44.99187);
        ((GeneralPath) shape).lineTo(40.894115, 33.249683);
        ((GeneralPath) shape).lineTo(40.683178, 33.249683);
        ((GeneralPath) shape).lineTo(36.964428, 41.85906);
        ((GeneralPath) shape).closePath();

        g.fill(shape);

        g.setTransform(transformations.poll()); // _0_0_1

    }

    /**
     * Returns the X of the bounding box of the original SVG image.
     * 
     * @return The X of the bounding box of the original SVG image.
     */
    public static int getOrigX() {
        return 9;
    }

    /**
     * Returns the Y of the bounding box of the original SVG image.
     * 
     * @return The Y of the bounding box of the original SVG image.
     */
    public static int getOrigY() {
        return 9;
    }

    /**
     * Returns the width of the bounding box of the original SVG image.
     * 
     * @return The width of the bounding box of the original SVG image.
     */
    public static int getOrigWidth() {
        return 82;
    }

    /**
     * Returns the height of the bounding box of the original SVG image.
     * 
     * @return The height of the bounding box of the original SVG image.
     */
    public static int getOrigHeight() {
        return 82;
    }
}


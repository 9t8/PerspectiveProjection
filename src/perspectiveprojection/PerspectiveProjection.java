/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perspectiveprojection;

import Jama.Matrix;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author David
 */
public class PerspectiveProjection extends JPanel {

    private static final Logger LOG = Logger.getLogger(PerspectiveProjection.class.getName());
    private static final long serialVersionUID = 1L;

    private static final Matrix VERTICES = new Matrix(new double[][]{
        // 0    1    2    3    4    5    6    7     8     9    10    11
        {000, 100, 000, 000, 100, 100, 100, 100, -100, -100, -100, -100},
        {200, 200, 300, 200, 300, 300, 100, 100, 300, 300, 100, 100},
        {000, 000, 000, 100, 100, -100, 100, -100, 100, -100, 100, -100},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    });

    private static final int[][] EDGES = new int[][]{
        {0, 1},
        {0, 2},
        {0, 3},
        {1, 2},
        {1, 3},
        {2, 3},
        {4, 5},
        {5, 7},
        {7, 6},
        {6, 4},
        {8, 9},
        {9, 11},
        {11, 10},
        {10, 8},
        {4, 8},
        {5, 9},
        {6, 10},
        {7, 11}
    };

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Keyboard k = new Keyboard(new int[]{
            KeyEvent.VK_DOWN,
            KeyEvent.VK_UP,
            KeyEvent.VK_LEFT,
            KeyEvent.VK_RIGHT,
            KeyEvent.VK_A,
            KeyEvent.VK_D,
            KeyEvent.VK_S,
            KeyEvent.VK_W,
            KeyEvent.VK_SHIFT,
            KeyEvent.VK_SPACE
        });
        jf.addKeyListener(k);

        PerspectiveProjection pp = new PerspectiveProjection(k);
        jf.add(pp);

        jf.pack();
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);

        pp.run();
    }

    private final Keyboard k;
    private final Matrix camCoords;

    private double camYaw;
    private double camPitch;

    public PerspectiveProjection(Keyboard k) {
        this.k = k;
        camCoords = new Matrix(3, 1);

        setPreferredSize(new Dimension(800, 600));
    }

    public void run() {
        for (long prev = new Date().getTime(), lag = 0;; repaint()) {
            long curr = new Date().getTime();
            lag += curr - prev;
            prev = curr;

            for (; lag >= 5; lag -= 5) {
                if (k.get(KeyEvent.VK_DOWN)) {
                    camPitch = Math.max(camPitch - 0.02, -Math.PI / 2);
                }
                if (k.get(KeyEvent.VK_UP)) {
                    camPitch = Math.min(camPitch + 0.02, Math.PI / 2);
                }
                if (k.get(KeyEvent.VK_LEFT)) {
                    camYaw = (camYaw + .02) % (2 * Math.PI);
                }
                if (k.get(KeyEvent.VK_RIGHT)) {
                    camYaw = (camYaw - .02) % (2 * Math.PI);
                }
                if (k.get(KeyEvent.VK_A)) {
                    camCoords.set(0, 0, camCoords.get(0, 0) - Math.cos(camYaw));
                    camCoords.set(1, 0, camCoords.get(1, 0) - Math.sin(camYaw));
                }
                if (k.get(KeyEvent.VK_D)) {
                    camCoords.set(0, 0, camCoords.get(0, 0) + Math.cos(camYaw));
                    camCoords.set(1, 0, camCoords.get(1, 0) + Math.sin(camYaw));
                }
                if (k.get(KeyEvent.VK_S)) {
                    camCoords.set(0, 0, camCoords.get(0, 0) + Math.sin(camYaw));
                    camCoords.set(1, 0, camCoords.get(1, 0) - Math.cos(camYaw));
                }
                if (k.get(KeyEvent.VK_W)) {
                    camCoords.set(0, 0, camCoords.get(0, 0) - Math.sin(camYaw));
                    camCoords.set(1, 0, camCoords.get(1, 0) + Math.cos(camYaw));
                }
                if (k.get(KeyEvent.VK_SHIFT)) {
                    camCoords.set(2, 0, camCoords.get(2, 0) - 1);
                }
                if (k.get(KeyEvent.VK_SPACE)) {
                    camCoords.set(2, 0, camCoords.get(2, 0) + 1);
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Matrix translation = Matrix.identity(4, 4);
        translation.setMatrix(0, 2, 3, 3, camCoords.uminus());

        Matrix yawRotation = Matrix.identity(4, 4);
        yawRotation.setMatrix(0, 1, 0, 1, new Matrix(new double[][]{
            {Math.cos(camYaw), Math.sin(camYaw)},
            {-Math.sin(camYaw), Math.cos(camYaw)}
        }));

        Matrix pitchRotation = Matrix.identity(4, 4);
        pitchRotation.setMatrix(1, 2, 1, 2, new Matrix(new double[][]{
            {Math.cos(camPitch), Math.sin(camPitch)},
            {-Math.sin(camPitch), Math.cos(camPitch)}
        }));

        Matrix mappedVertices
                = pitchRotation.times(yawRotation.times(translation.times(VERTICES)));

        ProjectedVertex[] projectedVertices = new ProjectedVertex[mappedVertices.getColumnDimension()];
        for (int i = 0; i < projectedVertices.length; ++i) {
            double scale = getHeight() / 2. / Math.max(mappedVertices.get(1, i), .01);
            projectedVertices[i] = new ProjectedVertex(
                    (int) (scale * mappedVertices.get(0, i) + getWidth() / 2.),
                    (int) (-scale * mappedVertices.get(2, i) + getHeight() / 2.),
                    mappedVertices.get(1, i) > 0
            );
        }

        for (int i = 0; i < EDGES.length; ++i) {
            if (projectedVertices[EDGES[i][0]].isInFront()
                    || projectedVertices[EDGES[i][1]].isInFront()) {
                g.drawLine(
                        projectedVertices[EDGES[i][0]].getX(),
                        projectedVertices[EDGES[i][0]].getY(),
                        projectedVertices[EDGES[i][1]].getX(),
                        projectedVertices[EDGES[i][1]].getY()
                );
            }
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perspectiveprojection;

import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class ProjectedVertex {

    private static final Logger LOG = Logger.getLogger(ProjectedVertex.class.getName());

    private final int x, y;
    private final boolean inFront;

    public ProjectedVertex(int x, int y, boolean inFront) {
        this.x = x;
        this.y = y;
        this.inFront = inFront;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @return the inFront
     */
    public boolean isInFront() {
        return inFront;
    }

}

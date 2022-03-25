/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perspectiveprojection;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 *
 * @author David
 */
public class Keyboard extends KeyAdapter {

    private static final Logger LOG = Logger.getLogger(Keyboard.class.getName());

    private final HashMap<Integer, Boolean> statuses;

    public Keyboard(int[] keys) {
        statuses = new HashMap<>(keys.length);
        for (int key : keys) {
            statuses.put(key, false);
        }
    }

    public boolean get(int key) {
        return statuses.get(key);
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (statuses.containsKey(ke.getKeyCode())) {
            statuses.put(ke.getKeyCode(), true);
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        if (statuses.containsKey(ke.getKeyCode())) {
            statuses.put(ke.getKeyCode(), false);
        }
    }

}

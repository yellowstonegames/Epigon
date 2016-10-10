package squidpony.funzone;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 * Test to read in and display map data.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class MapDisplayer {

    private JFrame frame;
    private JTextArea display;
    private JComboBox box;
    private HashMap<String, RoomBlueprint> roomMap = new HashMap<>();

    public static void main(String... args) {
        new MapDisplayer().go();
    }

    /**
     * Runs the data compiler and then displays the maps found.
     */
    private void go() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        display = new JTextArea("Reading files.");
        frame.add(display);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        TemplateDataCompiler.go();
        ArrayList<RoomTemplate> rooms = CompilerDataMaster.INSTANCE.rooms;

        //display all the rooms in the console
        for (RoomTemplate room : rooms) {
            roomMap.put(room.name, room);
        }

        box = new JComboBox(roomMap.keySet().toArray());
        frame.remove(display);
        frame.add(box, BorderLayout.NORTH);
        frame.pack();
        frame.repaint();

        box.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                doMapSelected();
            }
        });
        doMapSelected();
    }

    private void doMapSelected() {
        if (display != null) {
            frame.remove(display);
        }
        char[][] blueprint = roomMap.get((String) box.getSelectedItem()).getBlueprint();
        if (blueprint != null) {
            display = new JTextArea(blueprint[0].length, blueprint.length);
            String text = "";
            for (int y = 0; y < blueprint[0].length; y++) {
                for (int x = 0; x < blueprint.length; x++) {
                    text += blueprint[x][y];
                }
                text += '\n';
            }
            display.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            display.setText(text);
            frame.add(display, BorderLayout.SOUTH);
            frame.pack();
            frame.setLocationRelativeTo(null);
        }
    }
}

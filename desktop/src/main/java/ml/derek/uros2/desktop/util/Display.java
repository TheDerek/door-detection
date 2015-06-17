package ml.derek.uros2.desktop.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Display
{
    public static void image(BufferedImage... imgs)
    {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());

        for(BufferedImage image : imgs)
            frame.getContentPane().add(new JLabel(new ImageIcon(image)));

        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}

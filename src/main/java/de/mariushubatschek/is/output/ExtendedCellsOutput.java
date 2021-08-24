package de.mariushubatschek.is.output;

import de.mariushubatschek.is.algorithms.util.ExtendedCells;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ExtendedCellsOutput {

    private Image tent;

    private Image tree;

    private Image blank;

    private Image undetermined;

    public ExtendedCellsOutput() throws IOException {
        tent = ImageIO.read(ExtendedCellsOutput.class.getResource("/images/tent.png"));
        tree = ImageIO.read(ExtendedCellsOutput.class.getResource("/images/tree.png"));
        blank = ImageIO.read(ExtendedCellsOutput.class.getResource("/images/blank.png"));
        undetermined = ImageIO.read(ExtendedCellsOutput.class.getResource("/images/undetermined.png"));
    }

    public BufferedImage output(ExtendedCells[][] tiles, int[] rowHints, int[] colHints) {
        int offset = 15;
        int tileWidth = 32;
        int tileHeight = 32;
        int imageWidth = tiles[0].length * tileWidth + offset;
        int imageHeight = tiles.length * tileHeight + offset;
        BufferedImage targetImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = targetImage.getGraphics();
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.fillRect(0, 0, imageWidth, imageHeight);

        graphics.setColor(Color.BLACK);
        for (int j = 0; j < colHints.length; j++) {
            graphics.drawString(colHints[j]+"", offset + j*tileWidth, offset - (offset / 4));
        }

        for (int i = 0; i < tiles.length; i++) {
            int yStart = i * tileHeight + offset;
            int yEnd = (i+1) * tileHeight + offset;
            for (int j = 0; j < tiles[i].length; j++) {
                int xStart = j * tileWidth;
                int xEnd = (j+1) * tileWidth;
                Image image1 = null;
                switch (tiles[i][j]) {
                    case BLANK:
                        image1 = blank;
                        break;
                    case TENT:
                        image1 = tent;
                        break;
                    case TREE:
                        image1 = tree;
                        break;
                    case UNDETERMINED:
                        image1 = undetermined;
                        break;
                }
                graphics.drawImage(image1, xStart, yStart, xEnd, yEnd, 0, 0, 32, 32, null);
            }
            graphics.drawString(rowHints[i]+"", (int) (imageWidth - offset*0.7), tileWidth + offset/4 + i*tileWidth);
        }
        return targetImage;
    }

}

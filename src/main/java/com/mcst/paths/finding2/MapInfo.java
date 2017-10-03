package com.mcst.paths.finding2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 * Created by bocharov_n on 10.02.17.
 */
public class MapInfo {
    private BufferedImage passabilityMap;
    private BufferedImage imageMap;
    private byte[][] passabilityArray = null;
    private MapColors mapColors = new MapColors();

    private int scale;
    private int rotationAngle;

    public MapInfo() {
        imageMap = null;
        passabilityMap = null;
        passabilityArray = null;
        scale = 15;
        rotationAngle = 15;
    }

    public int calculatePassability() {
        if (imageMap == null) {
            return 0;
        }
        passabilityMap = new BufferedImage(imageMap.getWidth(), imageMap.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = passabilityMap.createGraphics();
        int average = 0;
        boolean isBlack = false;
        passabilityArray = new byte[imageMap.getWidth()][imageMap.getHeight()];
        for (int i = 0; i < passabilityArray.length; ++i) {
            for (int j = 0; j < passabilityArray[0].length; ++j) {
                passabilityArray[i][j] = -128;
            }
        }
        for (int i = scale; i <= imageMap.getHeight(); i += scale) {
            for (int j = scale; j <= imageMap.getWidth(); j += scale) {
                int oldPassability = 100;
                for (int x = j - scale; x < j; ++x) {
                    for (int y = i - scale; y < i; ++y) {
                        Color color = new Color(imageMap.getRGB(x, y));
                        int passability = mapColors.getPassability(color);
                        if (passability == 0) {
                            int aroundPassability = checkAround(new Point(x, y));
                            if (aroundPassability == 0) {
                                average = 0;
                                isBlack = true;
                                break;
                            } else {
                                average += aroundPassability;
                            }
                        } else if (passability == -1) {
                            average += oldPassability;
                        } else if (passability > 0) {
                            oldPassability = passability;
                            average += passability;
                        }
                    }
                    if (isBlack) {
                        isBlack = false;
                        break;
                    }
                }

                average = average / (scale * scale);

                g2d.setColor(new Color(average, average, average));
                g2d.fillRect(j - scale, i - scale, scale, scale);

                for (int x = j - scale; x < j; ++x) {
                    for (int y = i - scale; y < i; ++y) {
                        passabilityArray[x][y] = (byte) (average - 128);
                    }
                }
                average = 0;
            }
        }

        g2d.dispose();
        return 1;
    }

    private int checkAround(Point point) {
        int average = 0;
        int counter = 0;
        int current;
        for (int xi = point.x - 1; xi <= point.x + 1; ++xi) {
            for (int yi = point.y - 1; yi <= point.y + 1; ++yi) {
                if (xi < 0 || xi == point.x || xi >= imageMap.getWidth() || yi < 0 ||
                        yi == point.y || yi >= imageMap.getHeight()) {
                    continue;
                }
                Color color = new Color(imageMap.getRGB(xi, yi));
                if ((current = mapColors.getPassability(color)) == 0) {
                    return 0;
                }
                average += current;
                counter++;
            }
        }
        return average / counter;
    }

    public int getPointWeight(Point point, int size, double azimuth) {
        if (passabilityArray == null) {
            return 255;
        }
        int robotSize = scale / 2;
        int dx = robotSize - 2;
        int dy = dx - robotSize / 3;
        int[] arrayX = {dx, -dx, -dx, dx, 0, 0};
        int[] arrayY = {dy, dy, -dy, -dy, -dy, dy};
        int weight = 0;
        int length = passabilityArray[0].length;
        double cos = Math.cos(azimuth);
        double sin = Math.sin(azimuth);

        // rotating and checking
        for (int i = 0; i < 6; ++i) {
            dx = arrayX[i];
            dy = arrayY[i];
            int xi = point.x + (int) (cos * dx + sin * dy);
            int yi = point.y + (int) (cos * dy - sin * dx);

            if ((xi | yi) < 0) {
                return 255;
            }
            if (xi < passabilityArray.length && yi < length) {
                int add = passabilityArray[xi][yi];
                if (add == -128) {
                    return 255; // point is blocked
                }
                weight += 127 - add;
            } else if (xi <= passabilityArray.length + robotSize && yi <= length + robotSize) {
                weight += 254;
            } else {
                return 255;
            }
        }
        return weight / 4;
    }

    public BufferedImage getImageMap() {
        return imageMap;
    }

    public void setImageMap(BufferedImage imageMap) {
        this.imageMap = imageMap;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(int rotationAngle) {
        this.rotationAngle = rotationAngle;
    }

    public BufferedImage getPassabilityMap() {
        return passabilityMap;
    }

    public void setPassabilityMap(BufferedImage passabilityMap) {
        this.passabilityMap = passabilityMap;
    }
}

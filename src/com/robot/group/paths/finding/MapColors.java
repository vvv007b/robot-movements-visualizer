package com.robot.group.paths.finding;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map.Entry;

// Maps can be downloaded at http://overpass-turbo.eu/
class MapColors {
    // ����������� "����"-"����������� ������������"
    HashMap<Color, Integer> colorMap;
    // ����� �������� �������� ������������ � ���������� �� ����� ������������
    //public static final int DIFFERENCE_BORDER=20;
    public static final int DIFFERENCE_BORDER = 128;
    // ����� ������, ��� ������� ���� �����������
    public static final int GRAY_BORDER = 128;

    //public static final float compareBorder=0.003f;
//    public static final float compareBorder = 0.01f;

    public MapColors() {
        colorMap = new HashMap<>();

        colorMap.put(new Color(130, 156, 193), 250);    // motorway
        colorMap.put(new Color(113, 178, 113), 250);    // trunk road
        colorMap.put(new Color(127, 201, 127), 250);    // trunk road
        colorMap.put(new Color(212, 101, 104), 250);    // primary road
        colorMap.put(new Color(228, 109, 113), 250);    // primary road
        colorMap.put(new Color(236, 178, 103), 250);    // secondary road
        colorMap.put(new Color(253, 191, 111), 250);    // secondary road
        colorMap.put(new Color(248, 248, 186), 250);    // primary road (manually detected)
        colorMap.put(new Color(220, 158, 158), 250);    // primary road (manually detected)
        colorMap.put(new Color(248, 213, 170), 250);    // secondary road (manually detected)

        colorMap.put(new Color(246, 238, 182), 230);    // parking (manually detected)

        colorMap.put(new Color(226, 194, 168), 240);    // unsurfaced road
        colorMap.put(new Color(222, 186, 156), 240);    // unsurfaced road

        colorMap.put(new Color(168, 125, 39), 200);        // track

        colorMap.put(new Color(255, 211, 39), 170);        // byway

        colorMap.put(new Color(87, 177, 87), 150);        // bridleway

        colorMap.put(new Color(121, 123, 245), 220);    // cycleway
        colorMap.put(new Color(249, 117, 112), 220);    // footway
        colorMap.put(new Color(249, 131, 117), 220);    // footway (manually detected)

        //colorMap.put(new Color(153, 153, 153), 0);    // railway

        colorMap.put(new Color(188, 188, 205), 250);    // airport runway and taxiway

        colorMap.put(new Color(141, 197, 108), 80);        // forest

        colorMap.put(new Color(174, 209, 160), 70);        // wood

        colorMap.put(new Color(205, 246, 201), 110);    // grass (manually detected)

        colorMap.put(new Color(181, 227, 181), 120);    // golf course

        colorMap.put(new Color(182, 253, 182), 100);    // park

        colorMap.put(new Color(204, 204, 204), 150);    // residential area

        colorMap.put(new Color(207, 236, 168), 100);    // common and meadow

        colorMap.put(new Color(241, 218, 218), 150);    // retail area
        colorMap.put(new Color(255, 174, 185), 150);    // industrial area
        colorMap.put(new Color(239, 200, 200), 150);    // commercial area
        colorMap.put(new Color(234, 218, 232), 150);    // some area (manually detected)

        colorMap.put(new Color(255, 255, 192), 150);    // heathland

        colorMap.put(new Color(181, 208, 208), 0);        // lake and reservoir

        colorMap.put(new Color(234, 216, 189), 120);    // farm

        colorMap.put(new Color(157, 157, 108), 0);        // brownfield site

        colorMap.put(new Color(200, 176, 132), 80);        // allotments

        colorMap.put(new Color(138, 211, 175), 120);    // sports pitch

        colorMap.put(new Color(51, 204, 153), 0);        // sports centre

        colorMap.put(new Color(171, 223, 150), 80);        // nature reserve

        colorMap.put(new Color(225, 143, 143), 150);    // military area
        colorMap.put(new Color(240, 240, 216), 150);    // school and university

        colorMap.put(new Color(204, 153, 153), 0);        // significant building
        colorMap.put(new Color(213, 208, 200), 0);        // building
        //colorMap.put(new Color(214, 209, 200), 0);        // building
        colorMap.put(new Color(216, 208, 201), 0);        // building
        //colorMap.put(new Color(205, 200, 190), 0);        // building

        colorMap.put(new Color(225, 225, 225), 150);        // areas
        colorMap.put(new Color(236, 236, 236), 150);        // areas (manually detected)

        //colorMap.put(new Color(254, 254, 254), 250);   // white (roads?)
        colorMap.put(new Color(242, 239, 233), 200);        // around roads?
        colorMap.put(new Color(255, 255, 255), 250);    // white (roads?)

    }

    // ���������� �������� ������������, ��������������� ����� color
    public int getPassability(Color color) {
        if (colorMap.containsKey(color)) {
            return colorMap.get(color);
        } else {
            for (Entry<Color, Integer> entry : colorMap.entrySet()) {
                // to make some optimization
                Color entryColor = entry.getKey();
                int entryRed = entryColor.getRed();
                int entryGreen = entryColor.getGreen();
                int entryBlue = entryColor.getBlue();
                if (color.getRed() - 1 <= entryRed && entryRed <= color.getRed() + 1 &&
                        color.getGreen() - 1 <= entryGreen && entryGreen <= color.getGreen() + 1 &&
                        color.getBlue() - 1 <= entryBlue && entryBlue <= color.getBlue() + 1) {
                    return entry.getValue();
                }
            }
        }
        return -1;
    }
}

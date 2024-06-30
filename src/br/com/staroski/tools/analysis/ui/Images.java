package br.com.staroski.tools.analysis.ui;

import static br.com.staroski.utils.CG.read;
import static br.com.staroski.utils.CG.resize;

import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Stores the {@link Image}s used by the {@link DispersionChartViewer}.
 *
 * @author Staroski, Ricardo Artur
 */
final class Images {

    public static final BufferedImage BALL_BLUE_12 = resize(read("/images/ball_blue_54x54.png"), 12, 12);

    public static final BufferedImage BALL_RED_16 = resize(read("/images/ball_red_54x54.png"), 16, 16);

    public static final BufferedImage SCATTERPLOT_BACKGROUND_1920 = read("/images/scatterplot_1920x1920.png");

    public static final BufferedImage TOOLS_16 = read("/images/tools_16x16.png");
    public static final BufferedImage TOOLS_32 = read("/images/tools_32x32.png");
    public static final BufferedImage TOOLS_64 = read("/images/tools_64x64.png");
    public static final BufferedImage TOOLS_128 = read("/images/tools_128x128.png");
    public static final BufferedImage TOOLS_256 = read("/images/tools_256x256.png");

    public static final BufferedImage ACTION_COPY = read("/images/copy_48x48.png");

    public static final BufferedImage LANGUAGE_PT_BR_40 = read("/images/flag_brazil_40x40.png");
    public static final BufferedImage LANGUAGE_EN_US_40 = read("/images/flag_usa_40x40.png");
    public static final BufferedImage LANGUAGE_DE_DE_40 = read("/images/flag_germany_40x40.png");

    private Images() {}
}

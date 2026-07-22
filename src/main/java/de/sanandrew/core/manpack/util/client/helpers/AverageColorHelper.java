/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.core.manpack.util.client.helpers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

@SideOnly(value=Side.CLIENT)
public final class AverageColorHelper {
    public static SAPUtils.RGBAValues getAverageColor(InputStream is) throws IOException {
        return AverageColorHelper.getAverageColor(is, null);
    }

    public static SAPUtils.RGBAValues getAverageColor(InputStream is, SAPUtils.RGBAValues maskClr) throws IOException {
        BufferedImage bi = ImageIO.read(is);
        double red = 0.0;
        double green = 0.0;
        double blue = 0.0;
        double count = 0.0;
        for (int x = 0; x < bi.getWidth(); ++x) {
            for (int y = 0; y < bi.getHeight(); ++y) {
                SAPUtils.RGBAValues color = new SAPUtils.RGBAValues(bi.getRGB(x, y));
                if (color.getAlpha() == 0 || maskClr != null && color.getRed() == maskClr.getRed() && color.getGreen() == maskClr.getGreen() && color.getBlue() == maskClr.getBlue()) continue;
                red += (double)color.getRed();
                green += (double)color.getGreen();
                blue += (double)color.getBlue();
                count += 1.0;
            }
        }
        int avgRed = (int)(red / count);
        int avgGreen = (int)(green / count);
        int avgBlue = (int)(blue / count);
        return new SAPUtils.RGBAValues(avgRed, avgGreen, avgBlue, 255);
    }
}


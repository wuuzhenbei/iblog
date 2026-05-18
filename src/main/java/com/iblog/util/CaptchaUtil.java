package com.iblog.util;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class CaptchaUtil {
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LEN = 4;
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    public static String generate(HttpServletResponse response) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        Random random = new Random();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LEN; i++) {
            String ch = String.valueOf(CHARS.charAt(random.nextInt(CHARS.length())));
            code.append(ch);
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g.setFont(new Font("Arial", Font.BOLD, 24 + random.nextInt(8)));
            g.drawString(ch, 20 + i * 25, 28 + random.nextInt(6));
        }

        for (int i = 0; i < 6; i++) {
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g.drawLine(random.nextInt(WIDTH), random.nextInt(HEIGHT), random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }

        g.dispose();
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache");
        ImageIO.write(image, "png", response.getOutputStream());

        return code.toString();
    }
}

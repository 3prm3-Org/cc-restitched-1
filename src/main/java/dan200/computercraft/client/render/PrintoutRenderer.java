/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import dan200.computercraft.client.gui.FixedWidthFontRenderer;
import dan200.computercraft.core.terminal.TextBuffer;
import dan200.computercraft.shared.util.Palette;
import net.minecraft.client.renderer.texture.OverlayTexture;

import static dan200.computercraft.client.gui.FixedWidthFontRenderer.FONT_HEIGHT;
import static dan200.computercraft.shared.media.items.ItemPrintout.LINES_PER_PAGE;

public final class PrintoutRenderer
{
    private static final float BG_SIZE = 256.0f;

    /**
     * Width of a page.
     */
    public static final int X_SIZE = 172;

    /**
     * Height of a page.
     */
    public static final int Y_SIZE = 209;

    /**
     * Padding between the left and right of a page and the text.
     */
    public static final int X_TEXT_MARGIN = 11;

    /**
     * Padding between the top and bottom of a page and the text.
     */
    public static final int Y_TEXT_MARGIN = 10;

    /**
     * Width of the extra page texture.
     */
    private static final int X_FOLD_SIZE = 12;

    /**
     * Size of the leather cover.
     */
    public static final int COVER_SIZE = 12;

    private static final int COVER_Y = Y_SIZE;
    private static final int COVER_X = X_SIZE + 4 * X_FOLD_SIZE;

    private PrintoutRenderer() {}

    public static void drawText( PoseStack transform, VertexConsumer buffer, int x, int y, int start, int light, TextBuffer[] text, TextBuffer[] colours )
    {
        for( int line = 0; line < LINES_PER_PAGE && line < text.length; line++ )
        {
            FixedWidthFontRenderer.drawString( transform, buffer,
                x, y + line * FONT_HEIGHT, text[start + line], colours[start + line], null, Palette.DEFAULT,
                false, 0, 0,
                light
            );
        }
    }

    public static void drawText( PoseStack transform, VertexConsumer buffer, int x, int y, int start, int light, String[] text, String[] colours )
    {
        for( int line = 0; line < LINES_PER_PAGE && line < text.length; line++ )
        {
            FixedWidthFontRenderer.drawString( transform, buffer,
                x, y + line * FONT_HEIGHT,
                new TextBuffer( text[start + line] ), new TextBuffer( colours[start + line] ),
                null, Palette.DEFAULT, false, 0, 0,
                light
            );
        }
    }

    public static void drawBorder( PoseStack transform, VertexConsumer buffer, float x, float y, float z, int page, int pages, boolean isBook, int light )
    {
        int leftPages = page;
        int rightPages = pages - page - 1;

        if( isBook )
        {
            // Border
            float offset = offsetAt( pages );
            float left = x - 4 - offset;
            float right = x + X_SIZE + offset - 4;

            // Left and right border
            drawTexture( transform, buffer, left - 4, y - 8, z - 0.02f, COVER_X, 0, COVER_SIZE, Y_SIZE + COVER_SIZE * 2, light );
            drawTexture( transform, buffer, right, y - 8, z - 0.02f, COVER_X + COVER_SIZE, 0, COVER_SIZE, Y_SIZE + COVER_SIZE * 2, light );

            // Draw centre panel (just stretched texture, sorry).
            drawTexture( transform, buffer,
                x - offset, y, z - 0.02f, X_SIZE + offset * 2, Y_SIZE,
                COVER_X + COVER_SIZE / 2.0f, COVER_SIZE, COVER_SIZE, Y_SIZE,
                light
            );

            float borderX = left;
            while( borderX < right )
            {
                double thisWidth = Math.min( right - borderX, X_SIZE );
                drawTexture( transform, buffer, borderX, y - 8, z - 0.02f, 0, COVER_Y, (float) thisWidth, COVER_SIZE, light );
                drawTexture( transform, buffer, borderX, y + Y_SIZE - 4, z - 0.02f, 0, COVER_Y + COVER_SIZE, (float) thisWidth, COVER_SIZE, light );
                borderX += thisWidth;
            }
        }

        // Current page background
        // z-offset is interleaved between the "zeroth" left/right page and the first left/right page, so that the
        // "bold" border can be drawn over the edge where appropriate.
        drawTexture( transform, buffer, x, y, z - 1e-3f * 0.5f, X_FOLD_SIZE * 2, 0, X_SIZE, Y_SIZE, light );

        // Left pages
        for( int n = 0; n <= leftPages; n++ )
        {
            drawTexture( transform, buffer,
                x - offsetAt( n ), y, z - 1e-3f * n,
                // Use the left "bold" fold for the outermost page
                n == leftPages ? 0 : X_FOLD_SIZE, 0,
                X_FOLD_SIZE, Y_SIZE, light
            );
        }

        // Right pages
        for( int n = 0; n <= rightPages; n++ )
        {
            drawTexture( transform, buffer,
                x + (X_SIZE - X_FOLD_SIZE) + offsetAt( n ), y, z - 1e-3f * n,
                // Two folds, then the main page. Use the right "bold" fold for the outermost page.
                X_FOLD_SIZE * 2 + X_SIZE + (n == rightPages ? X_FOLD_SIZE : 0), 0,
                X_FOLD_SIZE, Y_SIZE, light
            );
        }
    }

    private static void drawTexture( PoseStack transform, VertexConsumer buffer, float x, float y, float z, float u, float v, float width, float height, int light )
    {
        Matrix4f poseMatrix = transform.last().pose();
        Matrix3f normalMatrix = transform.last().normal();
        vertex( poseMatrix, normalMatrix, buffer, x, y + height, z, u / BG_SIZE, (v + height) / BG_SIZE, light );
        vertex( poseMatrix, normalMatrix, buffer, x + width, y + height, z, (u + width) / BG_SIZE, (v + height) / BG_SIZE, light );
        vertex( poseMatrix, normalMatrix, buffer, x + width, y, z, (u + width) / BG_SIZE, v / BG_SIZE, light );
        vertex( poseMatrix, normalMatrix, buffer, x, y, z, u / BG_SIZE, v / BG_SIZE, light );
    }

    private static void drawTexture( PoseStack transform, VertexConsumer buffer, float x, float y, float z, float width, float height, float u, float v, float tWidth, float tHeight, int light )
    {
        Matrix4f poseMatrix = transform.last().pose();
        Matrix3f normalMatrix = transform.last().normal();
        vertex( poseMatrix, normalMatrix, buffer, x, y + height, z, u / BG_SIZE, (v + tHeight) / BG_SIZE, light );
        vertex( poseMatrix, normalMatrix, buffer, x + width, y + height, z, (u + tWidth) / BG_SIZE, (v + tHeight) / BG_SIZE, light );
        vertex( poseMatrix, normalMatrix, buffer, x + width, y, z, (u + tWidth) / BG_SIZE, v / BG_SIZE, light );
        vertex( poseMatrix, normalMatrix, buffer, x, y, z, u / BG_SIZE, v / BG_SIZE, light );
    }

    private static void vertex( Matrix4f poseMatrix, Matrix3f normalMatrix, VertexConsumer buffer, float x, float y, float z, float u, float v, int light )
    {
        buffer.vertex( poseMatrix, x, y, z ).color( 255, 255, 255, 255 ).uv( u, v ).overlayCoords( OverlayTexture.NO_OVERLAY ).uv2( light ).normal( normalMatrix, 0f, 0f, 1f ).endVertex();
    }

    public static float offsetAt( int page )
    {
        return (float) (32 * (1 - Math.pow( 1.2, -page )));
    }
}

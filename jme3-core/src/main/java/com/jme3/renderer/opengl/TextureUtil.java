/*
 * Copyright (c) 2009-2014 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.renderer.opengl;

import com.jme3.renderer.Caps;
import com.jme3.renderer.RendererException;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.image.ColorSpace;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Internal utility class used by {@link GLRenderer} to manage textures.
 * 
 * @author Kirill Vainer
 */
final class TextureUtil {

    private static final Logger logger = Logger.getLogger(TextureUtil.class.getName());

    private final GL gl;
    private final GL2 gl2;
    private final GLExt glext;
    private GLImageFormat[][] formats;

    public TextureUtil(GL gl, GL2 gl2, GLExt glext) {
        this.gl = gl;
        this.gl2 = gl2;
        this.glext = glext;
    }
    
    public void initialize(EnumSet<Caps> caps) {
        StringBuilder sb = new StringBuilder();
        this.formats = GLImageFormats.getFormatsForCaps(caps);
        sb.append("Supported texture formats: \n");
        for (int i = 0; i < Format.values().length; i++) {
            Format format = Format.values()[i];
            if (formats[0][i] != null) {
                boolean srgb = formats[1][i] != null;
                sb.append("\t").append(format.toString());
                sb.append(" (Linear");
                if (srgb) sb.append("/sRGB");
                sb.append(")\n");
            }
        }
        logger.log(Level.INFO, sb.toString());
    }

    public GLImageFormat getImageFormat(Format fmt, boolean isSrgb) {
        if (isSrgb) {
            return formats[1][fmt.ordinal()];
        } else {
            return formats[0][fmt.ordinal()];
        }
    }

    public GLImageFormat getImageFormatWithError(Format fmt, boolean isSrgb) {
        GLImageFormat glFmt = getImageFormat(fmt, isSrgb);
        if (glFmt == null && isSrgb) {
            glFmt = getImageFormat(fmt, false);
            logger.log(Level.WARNING, "No sRGB format available for ''{0}''. Failling back to linear.", fmt);
        }
        if (glFmt == null) { 
            throw new RendererException("Image format '" + fmt + "' is unsupported by the video hardware.");
        }
        return glFmt;
    }
    
    private void uploadTextureLevel(GLImageFormat format, int target, int level, int slice, int sliceCount, int width, int height, int depth, int samples, ByteBuffer data) {
        if (format.compressed && data != null) {
            if (target == GL2.GL_TEXTURE_3D) {
                // For 3D textures, we upload the entire mipmap level.
                gl2.glCompressedTexImage3D(target,
                                           level,
                                           format.internalFormat,
                                           width,
                                           height,
                                           depth,
                                           0,
                                           data);
            } else if (target == GLExt.GL_TEXTURE_2D_ARRAY_EXT) {
                // For texture arrays, only upload 1 slice at a time.
                // zoffset specifies slice index, and depth is 1 to indicate
                // a single texture in the array.
                gl2.glCompressedTexSubImage3D(target,
                                              level,
                                              0,
                                              0,
                                              slice,
                                              width,
                                              height,
                                              1,
                                              format.internalFormat,
                                              data);
            } else {
                // Cubemaps also use 2D upload.
                gl2.glCompressedTexImage2D(target,
                                           level,
                                           format.internalFormat,
                                           width,
                                           height,
                                           0,
                                           data);
            }
        } else {
            // (Non-compressed OR allocating texture storage for FBO)
            if (target == GL2.GL_TEXTURE_3D) {
                gl2.glTexImage3D(target,
                                 level,
                                 format.internalFormat,
                                 width,
                                 height,
                                 depth,
                                 0,
                                 format.format,
                                 format.dataType,
                                 data);
            } else if (target == GLExt.GL_TEXTURE_2D_ARRAY_EXT) {
                if (slice == -1) {
                    // Allocate texture storage (data is NULL)
                    gl2.glTexImage3D(target,
                                     level,
                                     format.internalFormat,
                                     width,
                                     height,
                                     sliceCount, //# of slices
                                     0,
                                     format.format,
                                     format.dataType,
                                     data);
                } else {
                    // For texture arrays, only upload 1 slice at a time.
                    // zoffset specifies slice index, and depth is 1 to indicate
                    // a single texture in the array.
                    gl2.glTexSubImage3D(target,
                                        level,          // level
                                        0,              // xoffset
                                        0,              // yoffset
                                        slice,          // zoffset
                                        width,          // width
                                        height,         // height
                                        1,              // depth
                                        format.format,
                                        format.dataType,
                                        data);
                }
            } else {
                // 2D multisampled image.
                if (samples > 1) {
                    glext.glTexImage2DMultisample(target,
                                                  samples,
                                                  format.internalFormat,
                                                  width,
                                                  height,
                                                  true);
                } else {
                    // Regular 2D image
                    gl.glTexImage2D(target,
                                    level,
                                    format.internalFormat,
                                    width,
                                    height,
                                    0,
                                    format.format,
                                    format.dataType,
                                    data);
                }
            }
        }
    }

    public void uploadTexture(Image image,
                              int target,
                              int index,
                              boolean linearizeSrgb) {

        boolean getSrgbFormat = image.getColorSpace() == ColorSpace.sRGB && linearizeSrgb;
        Image.Format jmeFormat = image.getFormat();
        GLImageFormat oglFormat = getImageFormatWithError(jmeFormat, getSrgbFormat);

        ByteBuffer data;
        int sliceCount = 1;
        if (index >= 0 && image.getData() != null && image.getData().size() > 0) {
            data = image.getData(index);
            sliceCount = image.getData().size();
        } else {
            data = null;
        }

        int width = image.getWidth();
        int height = image.getHeight();
        int depth = image.getDepth();
        

        if (data != null) {
            gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
        }

        int[] mipSizes = image.getMipMapSizes();
        int pos = 0;
        // TODO: Remove unneccessary allocation
        if (mipSizes == null) {
            if (data != null) {
                mipSizes = new int[]{data.capacity()};
            } else {
                mipSizes = new int[]{width * height * jmeFormat.getBitsPerPixel() / 8};
            }
        }

        int samples = image.getMultiSamples();

        for (int i = 0; i < mipSizes.length; i++) {
            int mipWidth = Math.max(1, width >> i);
            int mipHeight = Math.max(1, height >> i);
            int mipDepth = Math.max(1, depth >> i);

            if (data != null) {
                data.position(pos);
                data.limit(pos + mipSizes[i]);
            }

            uploadTextureLevel(oglFormat, target, i, index, sliceCount, mipWidth, mipHeight, mipDepth, samples, data);

            pos += mipSizes[i];
        }
    }

    /**
     * Update the texture currently bound to target at with data from the given
     * Image at position x and y. The parameter index is used as the zoffset in
     * case a 3d texture or texture 2d array is being updated.
     *
     * @param image Image with the source data (this data will be put into the
     * texture)
     * @param target the target texture
     * @param index the mipmap level to update
     * @param x the x position where to put the image in the texture
     * @param y the y position where to put the image in the texture
     */
    /*
     public void uploadSubTexture(
     EnumSet<Caps> caps,
     Image image,
     int target,
     int index,
     int x,
     int y,
     boolean linearizeSrgb) {
     Image.Format fmt = image.getFormat();
     GLImageFormat glFmt = getImageFormatWithError(caps, fmt, image.getColorSpace() == ColorSpace.sRGB  && linearizeSrgb);

     ByteBuffer data = null;
     if (index >= 0 && image.getData() != null && image.getData().size() > 0) {
     data = image.getData(index);
     }

     int width = image.getWidth();
     int height = image.getHeight();
     int depth = image.getDepth();

     if (data != null) {
     gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
     }

     int[] mipSizes = image.getMipMapSizes();
     int pos = 0;

     // TODO: Remove unneccessary allocation
     if (mipSizes == null){
     if (data != null) {
     mipSizes = new int[]{ data.capacity() };
     } else {
     mipSizes = new int[]{ width * height * fmt.getBitsPerPixel() / 8 };
     }
     }

     int samples = image.getMultiSamples();

     for (int i = 0; i < mipSizes.length; i++){
     int mipWidth =  Math.max(1, width  >> i);
     int mipHeight = Math.max(1, height >> i);
     int mipDepth =  Math.max(1, depth  >> i);

     if (data != null){
     data.position(pos);
     data.limit(pos + mipSizes[i]);
     }

     // to remove the cumbersome if/then/else stuff below we'll update the pos right here and use continue after each
     // gl*Image call in an attempt to unclutter things a bit
     pos += mipSizes[i];

     int glFmtInternal = glFmt.internalFormat;
     int glFmtFormat = glFmt.format;
     int glFmtDataType = glFmt.dataType;

     if (glFmt.compressed && data != null){
     if (target == GL.GL_TEXTURE_3D){
     gl.glCompressedTexSubImage3D(target, i, x, y, index, mipWidth, mipHeight, mipDepth, glFmtInternal, data);
     continue;
     }

     // all other targets use 2D: array, cubemap, 2d
     gl.glCompressedTexSubImage2D(target, i, x, y, mipWidth, mipHeight, glFmtInternal, data);
     continue;
     }

     if (target == GL.GL_TEXTURE_3D){
     gl.glTexSubImage3D(target, i, x, y, index, mipWidth, mipHeight, mipDepth, glFmtFormat, glFmtDataType, data);
     continue;
     }

     if (target == GLExt.GL_TEXTURE_2D_ARRAY_EXT){
     // prepare data for 2D array or upload slice
     if (index == -1){
     gl.glTexSubImage3D(target, i, x, y, index, mipWidth, mipHeight, mipDepth, glFmtFormat, glFmtDataType, data);
     continue;
     }

     gl.glTexSubImage3D(target, i, x, y, index, width, height, 1, glFmtFormat, glFmtDataType, data);
     continue;
     }

     if (samples > 1){
     throw new IllegalStateException("Cannot update multisample textures");
     }

     gl.glTexSubImage2D(target, i, x, y, mipWidth, mipHeight, glFmtFormat, glFmtDataType, data);
     }
     }
     */
}

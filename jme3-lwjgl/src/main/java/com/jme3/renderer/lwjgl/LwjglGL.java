package com.jme3.renderer.lwjgl;

import com.jme3.renderer.opengl.GL;
import com.jme3.renderer.opengl.GL2;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

public class LwjglGL implements GL, GL2 {
    
    public void glActiveTexture(int param1) {
        GL13.glActiveTexture(param1);
    }

    public void glAlphaFunc(int param1, float param2) {
        GL11.glAlphaFunc(param1, param2);
    }

    public void glAttachShader(int param1, int param2) {
        GL20.glAttachShader(param1, param2);
    }

    public void glBindBuffer(int param1, int param2) {
        GL15.glBindBuffer(param1, param2);
    }

    public void glBindTexture(int param1, int param2) {
        GL11.glBindTexture(param1, param2);
    }

    public void glBlendFunc(int param1, int param2) {
        GL11.glBlendFunc(param1, param2);
    }

    public void glBufferData(int param1, FloatBuffer param2, int param3) {
        GL15.glBufferData(param1, param2, param3);
    }

    public void glBufferData(int param1, ShortBuffer param2, int param3) {
        GL15.glBufferData(param1, param2, param3);
    }

    public void glBufferData(int param1, ByteBuffer param2, int param3) {
        GL15.glBufferData(param1, param2, param3);
    }

    public void glBufferSubData(int param1, long param2, FloatBuffer param3) {
        GL15.glBufferSubData(param1, param2, param3);
    }

    public void glBufferSubData(int param1, long param2, ShortBuffer param3) {
        GL15.glBufferSubData(param1, param2, param3);
    }

    public void glBufferSubData(int param1, long param2, ByteBuffer param3) {
        GL15.glBufferSubData(param1, param2, param3);
    }

    public void glClear(int param1) {
        GL11.glClear(param1);
    }

    public void glClearColor(float param1, float param2, float param3, float param4) {
        GL11.glClearColor(param1, param2, param3, param4);
    }

    public void glColorMask(boolean param1, boolean param2, boolean param3, boolean param4) {
        GL11.glColorMask(param1, param2, param3, param4);
    }

    public void glCompileShader(int param1) {
        GL20.glCompileShader(param1);
    }

    public void glCompressedTexImage2D(int param1, int param2, int param3, int param4, int param5, int param6, ByteBuffer param7) {
        GL13.glCompressedTexImage2D(param1, param2, param3, param4, param5, param6, param7);
    }

    public void glCompressedTexImage3D(int param1, int param2, int param3, int param4, int param5, int param6, int param7, ByteBuffer param8) {
        GL13.glCompressedTexImage3D(param1, param2, param3, param4, param5, param6, param7, param8);
    }

    public void glCompressedTexSubImage2D(int param1, int param2, int param3, int param4, int param5, int param6, int param7, ByteBuffer param8) {
        GL13.glCompressedTexSubImage2D(param1, param2, param3, param4, param5, param6, param7, param8);
    }

    public void glCompressedTexSubImage3D(int param1, int param2, int param3, int param4, int param5, int param6, int param7, int param8, int param9, ByteBuffer param10) {
        GL13.glCompressedTexSubImage3D(param1, param2, param3, param4, param5, param6, param7, param8, param9, param10);
    }

    public int glCreateProgram() {
        return GL20.glCreateProgram();
    }

    public int glCreateShader(int param1) {
        return GL20.glCreateShader(param1);
    }

    public void glCullFace(int param1) {
        GL11.glCullFace(param1);
    }

    public void glDeleteBuffers(IntBuffer param1) {
        GL15.glDeleteBuffers(param1);
    }

    public void glDeleteProgram(int param1) {
        GL20.glDeleteProgram(param1);
    }

    public void glDeleteShader(int param1) {
        GL20.glDeleteShader(param1);
    }

    public void glDeleteTextures(IntBuffer param1) {
        GL11.glDeleteTextures(param1);
    }

    public void glDepthFunc(int param1) {
        GL11.glDepthFunc(param1);
    }

    public void glDepthMask(boolean param1) {
        GL11.glDepthMask(param1);
    }

    public void glDepthRange(double param1, double param2) {
        GL11.glDepthRange(param1, param2);
    }

    public void glDetachShader(int param1, int param2) {
        GL20.glDetachShader(param1, param2);
    }

    public void glDisable(int param1) {
        GL11.glDisable(param1);
    }

    public void glDisableVertexAttribArray(int param1) {
        GL20.glDisableVertexAttribArray(param1);
    }

    public void glDrawArrays(int param1, int param2, int param3) {
        GL11.glDrawArrays(param1, param2, param3);
    }

    public void glDrawBuffer(int param1) {
        GL11.glDrawBuffer(param1);
    }
    
    public void glDrawRangeElements(int param1, int param2, int param3, int param4, int param5, long param6) {
        GL12.glDrawRangeElements(param1, param2, param3, param4, param5, param6);
    }

    public void glEnable(int param1) {
        GL11.glEnable(param1);
    }

    public void glEnableVertexAttribArray(int param1) {
        GL20.glEnableVertexAttribArray(param1);
    }

    public void glGenBuffers(IntBuffer param1) {
        GL15.glGenBuffers(param1);
    }

    public void glGenTextures(IntBuffer param1) {
        GL11.glGenTextures(param1);
    }

    public void glGetBoolean(int param1, ByteBuffer param2) {
        GL11.glGetBoolean(param1, param2);
    }

    public int glGetError() {
        return GL11.glGetError();
    }
    
    public void glGetInteger(int param1, IntBuffer param2) {
        GL11.glGetInteger(param1, param2);
    }

    public void glGetProgram(int param1, int param2, IntBuffer param3) {
        GL20.glGetProgram(param1, param2, param3);
    }

    public void glGetShader(int param1, int param2, IntBuffer param3) {
        GL20.glGetShader(param1, param2, param3);
    }

    public String glGetString(int param1) {
        return GL11.glGetString(param1);
    }

    public boolean glIsEnabled(int param1) {
        return GL11.glIsEnabled(param1);
    }

    public void glLineWidth(float param1) {
        GL11.glLineWidth(param1);
    }

    public void glLinkProgram(int param1) {
        GL20.glLinkProgram(param1);
    }

    public void glPixelStorei(int param1, int param2) {
        GL11.glPixelStorei(param1, param2);
    }

    public void glPointSize(float param1) {
        GL11.glPointSize(param1);
    }

    public void glPolygonMode(int param1, int param2) {
        GL11.glPolygonMode(param1, param2);
    }

    public void glPolygonOffset(float param1, float param2) {
        GL11.glPolygonOffset(param1, param2);
    }

    public void glReadBuffer(int param1) {
        GL11.glReadBuffer(param1);
    }

    public void glReadPixels(int param1, int param2, int param3, int param4, int param5, int param6, ByteBuffer param7) {
        GL11.glReadPixels(param1, param2, param3, param4, param5, param6, param7);
    }

    public void glScissor(int param1, int param2, int param3, int param4) {
        GL11.glScissor(param1, param2, param3, param4);
    }

    public void glStencilFuncSeparate(int param1, int param2, int param3, int param4) {
        GL20.glStencilFuncSeparate(param1, param2, param3, param4);
    }

    public void glStencilOpSeparate(int param1, int param2, int param3, int param4) {
        GL20.glStencilOpSeparate(param1, param2, param3, param4);
    }

    public void glTexImage2D(int param1, int param2, int param3, int param4, int param5, int param6, int param7, int param8, ByteBuffer param9) {
        GL11.glTexImage2D(param1, param2, param3, param4, param5, param6, param7, param8, param9);
    }

    public void glTexImage3D(int param1, int param2, int param3, int param4, int param5, int param6, int param7, int param8, int param9, ByteBuffer param10) {
        GL12.glTexImage3D(param1, param2, param3, param4, param5, param6, param7, param8, param9, param10);
    }

    public void glTexParameterf(int param1, int param2, float param3) {
        GL11.glTexParameterf(param1, param2, param3);
    }

    public void glTexParameteri(int param1, int param2, int param3) {
        GL11.glTexParameteri(param1, param2, param3);
    }

    public void glTexSubImage2D(int param1, int param2, int param3, int param4, int param5, int param6, int param7, int param8, ByteBuffer param9) {
        GL11.glTexSubImage2D(param1, param2, param3, param4, param5, param6, param7, param8, param9);
    }

    public void glTexSubImage3D(int param1, int param2, int param3, int param4, int param5, int param6, int param7, int param8, int param9, int param10, ByteBuffer param11) {
        GL12.glTexSubImage3D(param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11);
    }

    public void glUniform1(int param1, FloatBuffer param2) {
        GL20.glUniform1(param1, param2);
    }

    public void glUniform1(int param1, IntBuffer param2) {
        GL20.glUniform1(param1, param2);
    }

    public void glUniform1f(int param1, float param2) {
        GL20.glUniform1f(param1, param2);
    }

    public void glUniform1i(int param1, int param2) {
        GL20.glUniform1i(param1, param2);
    }

    public void glUniform2(int param1, IntBuffer param2) {
        GL20.glUniform2(param1, param2);
    }

    public void glUniform2(int param1, FloatBuffer param2) {
        GL20.glUniform2(param1, param2);
    }

    public void glUniform2f(int param1, float param2, float param3) {
        GL20.glUniform2f(param1, param2, param3);
    }

    public void glUniform3(int param1, IntBuffer param2) {
        GL20.glUniform3(param1, param2);
    }

    public void glUniform3(int param1, FloatBuffer param2) {
        GL20.glUniform3(param1, param2);
    }

    public void glUniform3f(int param1, float param2, float param3, float param4) {
        GL20.glUniform3f(param1, param2, param3, param4);
    }

    public void glUniform4(int param1, FloatBuffer param2) {
        GL20.glUniform4(param1, param2);
    }

    public void glUniform4(int param1, IntBuffer param2) {
        GL20.glUniform4(param1, param2);
    }

    public void glUniform4f(int param1, float param2, float param3, float param4, float param5) {
        GL20.glUniform4f(param1, param2, param3, param4, param5);
    }

    public void glUniformMatrix3(int param1, boolean param2, FloatBuffer param3) {
        GL20.glUniformMatrix3(param1, param2, param3);
    }

    public void glUniformMatrix4(int param1, boolean param2, FloatBuffer param3) {
        GL20.glUniformMatrix4(param1, param2, param3);
    }

    public void glUseProgram(int param1) {
        GL20.glUseProgram(param1);
    }

    public void glVertexAttribPointer(int param1, int param2, int param3, boolean param4, int param5, long param6) {
        GL20.glVertexAttribPointer(param1, param2, param3, param4, param5, param6);
    }

    public void glViewport(int param1, int param2, int param3, int param4) {
        GL11.glViewport(param1, param2, param3, param4);
    }

    public int glGetAttribLocation(int param1, String param2) {
        return GL20.glGetAttribLocation(param1, param2);
    }

    public int glGetUniformLocation(int param1, String param2) {
        return GL20.glGetUniformLocation(param1, param2);
    }

    public void glShaderSource(int param1, String[] param2, IntBuffer param3) {
        GL20.glShaderSource(param1, param2);
    }

    public String glGetProgramInfoLog(int program, int maxSize) {
        return GL20.glGetProgramInfoLog(program, maxSize);
    }

    public String glGetShaderInfoLog(int shader, int maxSize) {
        return GL20.glGetShaderInfoLog(shader, maxSize);
    }
}

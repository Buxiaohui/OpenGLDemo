package com.example.buxiaohui.opengldemo;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.opengl.GLES30;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {
    boolean supportsEs2;
    GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        checkSupported();

        if (supportsEs2) {
            glSurfaceView = new GLSurfaceView(this);
            glSurfaceView.setRenderer(new GLRenderer());
            setContentView(glSurfaceView);
        } else {
            setContentView(R.layout.activity_main);
            Toast.makeText(this, "当前设备不支持OpenGL ES 2.0!", Toast.LENGTH_SHORT).show();
        }
    }


    private void checkSupported() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        supportsEs2 = configurationInfo.reqGlEsVersion >= 0x2000;

        boolean isEmulator = Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86"));

        supportsEs2 = supportsEs2 || isEmulator;
    }

    public class GLRenderer implements GLSurfaceView.Renderer {
        private float[] mTriangleArray = {
                0f, 1f, 0f,
                -1f, -1f, 0f,
                1f, -1f, 0f
        };
        //三角形各顶点颜色(三个顶点)
//                1, 1, 0, 1,/*255,255,0,255 黄色*/
//                0, 1, 1, 1,/*0,255,255,255 蓝色*/
//                1, 0, 1, 1 /*255,0,255,255 紫色*/
        private float[] mColor = new float[]{
                1, 1, 0, 1,
                0, 1, 1, 1,
                1, 0, 1, 1
        };
        private FloatBuffer mTriangleBuffer;
        private FloatBuffer mColorBuffer;


        public GLRenderer() {
            //点相关
            //先初始化buffer，数组的长度*4，因为一个float占4个字节
            ByteBuffer bb = ByteBuffer.allocateDirect(mTriangleArray.length * 4);
            //以本机字节顺序来修改此缓冲区的字节顺序
            bb.order(ByteOrder.nativeOrder());
            mTriangleBuffer = bb.asFloatBuffer();
            //将给定float[]数据从当前位置开始，依次写入此缓冲区
            mTriangleBuffer.put(mTriangleArray);
            //设置此缓冲区的位置。如果标记已定义并且大于新的位置，则要丢弃该标记。
            mTriangleBuffer.position(0);


            //颜色相关
            ByteBuffer bb2 = ByteBuffer.allocateDirect(mColor.length * 4);
            bb2.order(ByteOrder.nativeOrder());
            mColorBuffer = bb2.asFloatBuffer();
            mColorBuffer.put(mColor);
            mColorBuffer.position(0);
        }

        @Override
        public void onDrawFrame(GL10 gl) {


            // 清除屏幕和深度缓存
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            // 重置当前的模型观察矩阵
            gl.glLoadIdentity();

            // 允许设置顶点
            //GL10.GL_VERTEX_ARRAY顶点数组
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            // 允许设置颜色
            //GL10.GL_COLOR_ARRAY颜色数组
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

            //将三角形在z轴上移动
            gl.glTranslatef(-0.0f, 0.0f, -6.0f);

            // 设置三角形
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mTriangleBuffer);
            // 设置三角形颜色
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
            // 绘制三角形
            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);

            // 取消颜色设置
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
            // 取消顶点设置
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

            //绘制结束
            gl.glFinish();

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            float ratio = (float) width / height;
            // 设置OpenGL场景的大小,(0,0)表示窗口内部视口的左下角，(w,h)指定了视口的大小
            gl.glViewport(0, 0, width, height);
            // 设置投影矩阵
            gl.glMatrixMode(GL10.GL_PROJECTION);
            // 重置投影矩阵
            gl.glLoadIdentity();
            // 设置视口的大小
            gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);//confused wtf hahaha
            //以下两句声明，以后所有的变换都是针对模型(即我们绘制的图形)
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();

        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // 设置白色为清屏
            gl.glClearColor(1, 1, 1, 1);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (glSurfaceView != null) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (glSurfaceView != null) {
            glSurfaceView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (glSurfaceView != null) {
            glSurfaceView = null;
        }
    }
}

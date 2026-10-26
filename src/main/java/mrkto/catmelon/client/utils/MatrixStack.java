package mrkto.catmelon.client.utils;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;

public class MatrixStack {
    private final Matrix4f[] matrices;
    private final Matrix3f[] normalMatrices;

    private int currentDepth = 0;
    public MatrixStack(int maxDepth) {
        matrices = new Matrix4f[maxDepth];
        normalMatrices = new Matrix3f[maxDepth];

        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        Matrix3f matrix3f = new Matrix3f();
        matrix3f.setIdentity();
        for (int i = 0; i < maxDepth; i++) {
            matrices[i] = new Matrix4f(matrix4f);
            normalMatrices[i] = new Matrix3f(matrix3f);
        }
    }
    public void pop() {
        if(currentDepth == 0)
            throw new IllegalStateException("Stack is empty");

        currentDepth--;
    }
    public void push() {
        if(currentDepth == matrices.length - 1)
            throw new IllegalStateException("Stack is full");
        currentDepth++;
        matrices[currentDepth].set(matrices[currentDepth - 1]);
        normalMatrices[currentDepth].set(normalMatrices[currentDepth - 1]);
    }
    public Matrix4f getModelMatrix() {
        return matrices[currentDepth];
    }
    public Matrix3f getNormalMatrix() {
        return normalMatrices[currentDepth];
    }
    public int getDepth() {
        return currentDepth;
    }
    public void translate(float x, float y, float z) {
        Matrix4f model = getModelMatrix();

        model.m03 += model.m00 * x + model.m01 * y + model.m02 * z;
        model.m13 += model.m10 * x + model.m11 * y + model.m12 * z;
        model.m23 += model.m20 * x + model.m21 * y + model.m22 * z;
    }
    public void scale(float x, float y, float z) {
        Matrix4f model = getModelMatrix();

        model.m00 *= x;  model.m01 *= y;  model.m02 *= z;
        model.m10 *= x;  model.m11 *= y;  model.m12 *= z;
        model.m20 *= x;  model.m21 *= y;  model.m22 *= z;
        model.m30 *= x;  model.m31 *= y;  model.m32 *= z;

        if (x < 0 || y < 0 || z < 0) {
            Matrix3f normal = getNormalMatrix();
            if (x < 0)
                normal.m00 *= -1;
            if (y < 0)
                normal.m11 *= -1;
            if (z < 0)
                normal.m22 *= -1;
        }
    }
    public void rotateX(float radian) {
        float sin = (float) Math.sin(radian);
        float cos = (float)Math.cos(radian);

        Matrix4f model = getModelMatrix();
        float temp;

        temp = model.m01;
        model.m01 = temp * cos + model.m02 * sin;
        model.m02 = temp * -sin + model.m02 * cos;
        temp = model.m11;
        model.m11 = temp * cos + model.m12 * sin;
        model.m12 = temp * -sin + model.m12 * cos;
        temp = model.m21;
        model.m21 = temp * cos + model.m22 * sin;
        model.m22 = temp * -sin + model.m22 * cos;
        temp = model.m31;
        model.m31 = temp * cos + model.m32 * sin;
        model.m32 = temp * -sin + model.m32 * cos;

        Matrix3f normal = getNormalMatrix();

        temp = normal.m01;
        normal.m01 = temp * cos + normal.m02 * sin;
        normal.m02 = temp * -sin + normal.m02 * cos;
        temp = normal.m11;
        normal.m11 = temp * cos + normal.m12 * sin;
        normal.m12 = temp * -sin + normal.m12 * cos;
        temp = normal.m21;
        normal.m21 = temp * cos + normal.m22 * sin;
        normal.m22 = temp * -sin + normal.m22 * cos;
    }

    public void rotateY(float radian) {
        float sin = (float) Math.sin(radian);
        float cos = (float) Math.cos(radian);

        Matrix4f model = getModelMatrix();
        float temp;

        temp = model.m00;
        model.m00 = temp * cos + model.m02 * -sin;
        model.m02 = temp * sin + model.m02 * cos;
        temp = model.m10;
        model.m10 = temp * cos + model.m12 * -sin;
        model.m12 = temp * sin + model.m12 * cos;
        temp = model.m20;
        model.m20 = temp * cos + model.m22 * -sin;
        model.m22 = temp * sin + model.m22 * cos;
        temp = model.m30;
        model.m30 = temp * cos + model.m32 * -sin;
        model.m32 = temp * sin + model.m32 * cos;

        Matrix3f normal = getNormalMatrix();

        temp = normal.m00;
        normal.m00 = temp * cos + normal.m02 * -sin;
        normal.m02 = temp * sin + normal.m02 * cos;
        temp = normal.m10;
        normal.m10 = temp * cos + normal.m12 * -sin;
        normal.m12 = temp * sin + normal.m12 * cos;
        temp = normal.m20;
        normal.m20 = temp * cos + normal.m22 * -sin;
        normal.m22 = temp * sin + normal.m22 * cos;
    }

    public void rotateZ(float radian) {
        float sin = (float) Math.sin(radian);
        float cos = (float) Math.cos(radian);

        Matrix4f model = getModelMatrix();
        float temp;

        temp = model.m00;
        model.m00 = temp * cos + model.m01 * sin;
        model.m01 = temp * -sin + model.m01 * cos;

        temp = model.m10;
        model.m10 = temp * cos + model.m11 * sin;
        model.m11 = temp * -sin + model.m11 * cos;

        temp = model.m20;
        model.m20 = temp * cos + model.m21 * sin;
        model.m21 = temp * -sin + model.m21 * cos;

        temp = model.m30;
        model.m30 = temp * cos + model.m31 * sin;
        model.m31 = temp * -sin + model.m31 * cos;

        Matrix3f normal = getNormalMatrix();

        temp = normal.m00;
        normal.m00 = temp * cos + normal.m01 * sin;
        normal.m01 = temp * -sin + normal.m01 * cos;

        temp = normal.m10;
        normal.m10 = temp * cos + normal.m11 * sin;
        normal.m11 = temp * -sin + normal.m11 * cos;
        temp = normal.m20;
        normal.m20 = temp * cos + normal.m21 * sin;
        normal.m21 = temp * -sin + normal.m21 * cos;
    }
}

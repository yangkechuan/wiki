package com.example;


public class MyBitmap {

    // 每一个word是一个long类型元素，对应一个64位二进制数据
    private long[] words;

    // Bitmap的位数大小
    private int size;


    public MyBitmap(int size){
        this.size = size;
        this.words = new long[getWorldIndex(size - 1) + 1];
    }

    private int getWorldIndex(int bitIndex){
        // 右移6位，相当于除以64
        return bitIndex >> 6;
    }

    /**
     * 判断Bitmap某一位的状态
     * @param bitIndex  位图的第bitIndex位
     */
    private boolean getBit(int bitIndex){
        if (bitIndex < 0 || bitIndex > size - 1){
            throw new IndexOutOfBoundsException("超过Bitmap有效范围");
        }
        int wordIndex = getWorldIndex(bitIndex);
        return (words[wordIndex] & (1L << bitIndex)) != 0;
    }

    /**
     * 把Bitmap某一位设置为true
     * @param bitIndex 位图的第bitIndex位
     */
    private void setBit(int bitIndex){
        if (bitIndex < 0 || bitIndex > size - 1){
            throw new IndexOutOfBoundsException("超过Bitmap有效范围");
        }
        int wordIndex = getWorldIndex(bitIndex);
        words[wordIndex] |= (1L << bitIndex);
    }

    public static void main(String[] args) {
        MyBitmap bitmap = new MyBitmap(128);
        bitmap.setBit(126);
        bitmap.setBit(75);
        System.out.println(bitmap.getBit(126));
        System.out.println(bitmap.getBit(78));
    }
}

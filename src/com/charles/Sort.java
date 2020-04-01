package com.charles;

public class Sort{
    public static void qSort(int[] arr, int head, int tail) {
        if (head >= tail || arr == null || arr.length <= 1) {
            return;
        }
        int i = head, j = tail, pivot = arr[(head + tail) / 2];
        while (i <= j) {
            while (arr[i] < pivot) {
                ++i;
            }
            while (arr[j] > pivot) {
                --j;
            }
            if (i < j) {
                int t = arr[i];
                arr[i] = arr[j];
                arr[j] = t;
                ++i;
                --j;
            } else if (i == j) {
                ++i;
            }
        }
        qSort(arr, head, j);
        qSort(arr, i, tail);
    }

    static private void bubbleSort(int[] a) {
        int n = a.length;
        for(int i=0; i<n-1; i++) {
            for (int j=0; j<n-i-1; j++) {
                if (a[j] > a[j+1]) {
                    int t = a[j];
                    a[j] = a[j+1];
                    a[j+1] = t;
                }
            }
        }
    }

    static private void print(int[] a) {
        for (int i : a) {
            System.out.print(i + " ");
        }
        System.out.println();
    }

    public static void main(String []args){
        int [] a = {69, 55, 71, 99, 26, 34, 11, 5, 19};
        print(a);
        // bubbleSort(a);
        qSort(a, 0, a.length - 1);
        print(a);
    }
}
package io.github.pepelu.asmtarget.tackmethod;

/**
 * Created by sly on 2019-04-24.
 * name desc=<init>()V
 * name desc=f1()V
 * name desc=f2(I)V
 * name desc=f3(Ljava/lang/String;Ljava/lang/String;)V
 * name desc=f4(ILjava/lang/String;)V
 * name desc=f5(Ljava/lang/String;I)V
 * name desc=f6(ILjava/lang/String;I)V
 * name desc=f4()I
 */
public class TrackMethodTarget {
//    public static final String Tag = "aa";

    /**
     * 无参、无返回值
     */
    public void f1() {

    }

    public void f2(int i) {

    }

    public void f3(String s1, String s2) {

    }

    public void f4(int i, String s) {
    }

    public void f5(String s, int i) {

    }

    public void f6(int i, String s, int j) {

    }

    public int f7() {
        return 12;
    }
}

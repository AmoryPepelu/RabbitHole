package github.amorypepelu.clicktrack.classvisitor;

import com.android.tools.r8.org.objectweb.asm.ClassVisitor;
import com.android.tools.r8.org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import github.amorypepelu.clicktrack.methodvisitor.ViewClickMethodVisitor;

/**
 * Created by sly on 2019-04-20.
 */
public class DefaultClassVisitor extends ClassVisitor {

    private List<String> interfaces = new ArrayList<>();
    private String clzName;

    public DefaultClassVisitor(int api) {
        super(api);
    }

    public DefaultClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    /**
     * 扫描到类时第一个会执行的方法
     *
     * @param version    jdk 版本 51:1.7;52:1.8
     * @param access     类访问权限修饰符,ACC_
     * @param name       类名
     * @param signature  类泛型信息，如果未定义，此字段为空
     * @param superName  当前类所继承的父类
     * @param interfaces 该类所有实现的接口列表
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.clzName = name;
        super.visit(version, access, name, signature, superName, interfaces);
        if (interfaces != null && interfaces.length > 0) {
            Collections.addAll(this.interfaces, interfaces);
//            if (AsmConfig.isDebug) {
//                LogUtil.i("DefaultClassVisitor,name=" + name
//                        + ",signature=" + signature
//                        + ",superName=" + superName
//                        + ",interfaces=" + this.interfaces);
//            }
        }
    }

    /**
     * 扫描到类的方法时调用
     *
     * @param access     方法权限修饰符
     * @param name       方法名:onClick
     * @param descriptor 方法签名,包括返回值类型:(Landroid/view/View;)V
     * @param signature  泛型信息,可能为空
     * @param exceptions 抛出的异常,可能为空
     * @return
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        methodVisitor = new ViewClickMethodVisitor(clzName, interfaces, methodVisitor, access, name, descriptor);
        return methodVisitor;
    }
}

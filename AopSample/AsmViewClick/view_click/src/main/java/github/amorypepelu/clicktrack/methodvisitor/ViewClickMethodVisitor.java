package github.amorypepelu.clicktrack.methodvisitor;

import com.android.tools.r8.org.objectweb.asm.AnnotationVisitor;
import com.android.tools.r8.org.objectweb.asm.MethodVisitor;

import java.util.List;

import github.amorypepelu.clicktrack.config.AsmConfig;
import github.amorypepelu.clicktrack.util.LogUtil;

/**
 * Created by sly on 2019-04-22.
 */
public class ViewClickMethodVisitor extends DefaultMethodVisitor {

    private boolean isMatchWithAnnotation = false;

    /**
     * 继承的接口无效，必须是目标类中实现的接口才有此记录
     */
    private List<String> interfaces;

    /**
     * method name and params and return types:onClick(Landroid/view/View;)V
     * 方法名 + 形参 + 返回类型
     */
    private String nameDesc;

    /**
     * (Landroid/view/View;)V
     * 形参 + 返回类型
     */
    private String desc;

    private MethodVisitor methodVisitor;

    /**
     * 类名
     * io/github/pepelu/asmtarget/MainActivity$onCreate
     * io/github/pepelu/asmtarget/MainActivity$onCreate$3
     */
    private String clzName;

    public ViewClickMethodVisitor(String clzName,
                                  List<String> interfaces,
                                  MethodVisitor methodVisitor,
                                  int access,
                                  String name,
                                  String descriptor) {
        super(methodVisitor, access, name, descriptor);
        this.interfaces = interfaces;
        this.nameDesc = name + descriptor;
        this.desc = descriptor;
        this.methodVisitor = methodVisitor;
        this.clzName = clzName;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (AsmConfig.Annotation_Class.equals(descriptor)) {
            isMatchWithAnnotation = true;
        }
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        if ((interfaces.size() > 0) || isMatchWithAnnotation) {

            //太耗费性能了，小风扇噌噌噌地转
            if (AsmConfig.ext.isDebug) {
                LogUtil.i("ViewClickMethodVisitor,clssName=" + clzName
                        + ",nameDesc=" + nameDesc
                        + ",desc=" + desc
                        + ",isMatchWithAnnotation=" + isMatchWithAnnotation);
            }

            //View#setOnClickListener
            if ((interfaces.contains("android/view/View$OnClickListener")
                    && "onClick(Landroid/view/View;)V".equals(nameDesc))
                    || isMatchWithAnnotation) {
                methodVisitor.visitVarInsn(ALOAD, 1);//写入第一个参数:view
                methodVisitor.visitMethodInsn(INVOKESTATIC, AsmConfig.Hooker_Class, "trackViewOnClick", "(Landroid/view/View;)V", false);//写入static方法 trackViewOnClick(view)
            }
        }

//        test();
    }

    private static final String TrackMethodUtil = "io/github/pepelu/asmtarget/tackmethod/TrackMethodUtil";

    /**
     * 如果类型写错，会强转，但生成dex会报错，所以不要写错
     * javap -c clzFile.class
     */
    private void test() {
        if (clzName.equals("io/github/pepelu/asmtarget/tackmethod/TrackMethodTarget")) {
            LogUtil.i("modify io.github.pepelu.asmtarget.tackmethod.TrackMethodTarget,name desc=" + nameDesc);
            switch (nameDesc) {
                case "f1()V":
                    methodVisitor.visitVarInsn(ALOAD, 0);//不写入任何参数
                    methodVisitor.visitMethodInsn(INVOKESTATIC, TrackMethodUtil, "f1", "()V", false);
                    break;
                case "f2(I)V":
                    methodVisitor.visitVarInsn(ILOAD, 1);
                    methodVisitor.visitMethodInsn(INVOKESTATIC, TrackMethodUtil, "f2", "(I)V", false);
                    break;
                case "f3(Ljava/lang/String;Ljava/lang/String;)V":
                    methodVisitor.visitVarInsn(ALOAD, 1);
                    methodVisitor.visitVarInsn(ALOAD, 2);
                    methodVisitor.visitMethodInsn(INVOKESTATIC, TrackMethodUtil, "f3", "(Ljava/lang/String;Ljava/lang/String;)V", false);
                    break;
                case "f4(ILjava/lang/String;)V":
                    methodVisitor.visitVarInsn(ILOAD, 1);
                    methodVisitor.visitVarInsn(ALOAD, 2);
                    methodVisitor.visitMethodInsn(INVOKESTATIC, TrackMethodUtil, "f4", "(ILjava/lang/String;)V", false);
                    break;
                case "f5(Ljava/lang/String;I)V":
                    methodVisitor.visitVarInsn(ALOAD, 1);
                    methodVisitor.visitVarInsn(ILOAD, 2);
                    methodVisitor.visitMethodInsn(INVOKESTATIC, TrackMethodUtil, "f5", "(Ljava/lang/String;I)V", false);
                    break;
                case "f6(ILjava/lang/String;I)V":
                    methodVisitor.visitVarInsn(ILOAD, 1);
                    methodVisitor.visitVarInsn(ALOAD, 2);
                    methodVisitor.visitVarInsn(ILOAD, 3);
                    methodVisitor.visitMethodInsn(INVOKESTATIC, TrackMethodUtil, "f6", "(ILjava/lang/String;I)V", false);
                    break;
                default:
                    break;
            }
        }
    }

}

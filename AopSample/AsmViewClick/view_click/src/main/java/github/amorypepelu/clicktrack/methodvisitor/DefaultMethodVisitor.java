package github.amorypepelu.clicktrack.methodvisitor;

import com.android.tools.r8.org.objectweb.asm.AnnotationVisitor;
import com.android.tools.r8.org.objectweb.asm.Attribute;
import com.android.tools.r8.org.objectweb.asm.Handle;
import com.android.tools.r8.org.objectweb.asm.Label;
import com.android.tools.r8.org.objectweb.asm.MethodVisitor;
import com.android.tools.r8.org.objectweb.asm.Opcodes;
import com.android.tools.r8.org.objectweb.asm.TypePath;
import com.android.tools.r8.org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by sly on 2019-04-22.
 */
public class DefaultMethodVisitor extends AdviceAdapter {

    public DefaultMethodVisitor(MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(Opcodes.ASM5, methodVisitor, access, name, descriptor);
    }

    /**
     * 表示 ASM 开始扫描这个方法
     */
    @Override
    public void visitCode() {
        super.visitCode();
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

//    @Override
//    public void visitMethodInsn(int opcode, String owner, String name, String descriptor) {
//        super.visitMethodInsn(opcode, owner, name, descriptor);
//    }

    @Override
    public void visitAttribute(Attribute attribute) {
        super.visitAttribute(attribute);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        super.visitIntInsn(opcode, operand);
    }

    /**
     * 该方法是 visitEnd 之前调用的方法，可以反复调用。用以确定类方法在执行时候的堆栈大小。
     *
     * @param maxStack
     * @param maxLocals
     */
    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        super.visitVarInsn(opcode, var);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        super.visitMultiANewArrayInsn(descriptor, numDimensions);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return super.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
    }

    protected DefaultMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor);
    }

    @Override
    public void visitLabel(Label label) {
        super.visitLabel(label);
    }

    @Override
    public void visitLdcInsn(Object value) {
        super.visitLdcInsn(value);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
    }
}

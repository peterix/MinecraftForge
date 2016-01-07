package net.minecraftforge.fml.common.asm.transformers;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import net.minecraft.launchwrapper.IClassTransformer;

public class AnnotationHelperTransformer implements IClassTransformer
{
    private static final int ASM = Opcodes.ASM5;
    @Override
    public byte[] transform(String obfName, String transformedName, byte[] bytes)
    {
        if (bytes == null) return null;
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new ClassVisitor(ASM, classWriter)
        {
            public MethodVisitor visitMethod(int access, String mtdName, String mtdDesc, String signature, String[] exceptions)
            {
                return new MethodVisitor(ASM, super.visitMethod(access, mtdName, mtdDesc, signature, exceptions))
                {
                    public AnnotationVisitor visitAnnotation(final String annDesc, boolean visible)
                    {
                        return new CapAnnVisitor(super.visitAnnotation(annDesc, visible), annDesc);
                    }
                };
            }

            public FieldVisitor visitField(int access, String fdName, String fdDesc, String signature, Object value)
            {
                return new FieldVisitor(ASM, super.visitField(access, fdName, fdDesc, signature, value))
                {
                    public AnnotationVisitor visitAnnotation(final String annDesc, boolean visible)
                    {
                        return new CapAnnVisitor(super.visitAnnotation(annDesc, visible), annDesc);
                    }
                };
            }
        };
        classReader.accept(cv, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    private static class CapAnnVisitor extends AnnotationVisitor
    {
        private final String desc;
        public CapAnnVisitor(final AnnotationVisitor av, String desc)
        {
            super(ASM, av);
            this.desc = desc;
        }
        public void visit(String name, Object value)
        {
            if ("Lnet/minecraftforge/common/capabilities/CapabilityInject;".equals(desc))
            {
                // Transform: @CapabilityInject(String.class)
                // Into:      @CapabilityInject(className = "java.lang.String");
                if ("value".equals(name) && value != null)
                {
                    value = ((Type)value).getInternalName().replace('/', '.');
                    name = "className";
                }
            }
            super.visit(name, value);
        }
    }
}

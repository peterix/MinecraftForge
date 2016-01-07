package net.minecraftforge.common.capabilities;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.Level;
import org.objectweb.asm.Type;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

public enum CapabilityManager
{
    INSTANCE;

    private IdentityHashMap<String, Capability<?>> providers = Maps.newIdentityHashMap();
    private IdentityHashMap<String, List<Function<Capability<?>, Object>>> callbacks = Maps.newIdentityHashMap();

    public <T> Capability<T> register(Class<T> type, Capability.IStorage<T> storage, final Class<? extends T> implementation)
    {
        return register(type, storage, new Callable<T>()
        {
            @Override
            public T call() throws Exception
            {
                try {
                    return (T)implementation.newInstance();
                } catch (InstantiationException e) {
                    Throwables.propagate(e);
                } catch (IllegalAccessException e) {
                    Throwables.propagate(e);
                }
                return null;
            }
        });
    }

    public <T> Capability<T> register(Class<T> type, Capability.IStorage<T> storage, Callable<? extends T> factory)
    {
        String realName = type.getName().intern();
        Capability<T> cap = new Capability<T>(realName, storage, factory);
        providers.put(realName, cap);

        List<Function<Capability<?>, Object>> list = callbacks.get(realName);
        if (list != null)
        {
            for (Function<Capability<?>, Object> func : list)
            {
                func.apply(cap);
            }
        }
        return cap;
    }

    public void injectCapabilities(ASMDataTable data)
    {
        for (ASMDataTable.ASMData entry : data.getAll(CapabilityInject.class.getName()))
        {
            final String targetClass = entry.getClassName();
            final String targetName = entry.getObjectName();
            Type type = (Type)entry.getAnnotationInfo().get("value");
            if (type == null)
            {
                FMLLog.log(Level.WARN, "Unable to inject capability at %s.%s (Invalid Annotation)", targetClass, targetName);
            }
            final String capabilityName = type.getInternalName().replace('/', '.').intern();

            List<Function<Capability<?>, Object>> list = callbacks.get(capabilityName);
            if (list == null)
            {
                list = Lists.newArrayList();
                callbacks.put(capabilityName, list);
            }

            if (entry.getObjectName().indexOf('(') > 0)
            {
                list.add(new Function<Capability<?>, Object>()
                {
                    @Override
                    public Object apply(Capability<?> input)
                    {
                        try
                        {
                            for (Method mtd : Class.forName(targetClass).getDeclaredMethods())
                            {
                                if (targetName.equals(mtd.getName() + Type.getMethodDescriptor(mtd)))
                                {
                                    if ((mtd.getModifiers() & Modifier.STATIC) != Modifier.STATIC)
                                    {
                                        FMLLog.log(Level.WARN, "Unable to inject capability %s at %s.%s (Non-Static)", capabilityName, targetClass, targetName);
                                        return null;
                                    }

                                    mtd.setAccessible(true);
                                    mtd.invoke(null, input);
                                    return null;
                                }
                            }
                            FMLLog.log(Level.WARN, "Unable to inject capability %s at %s.%s (Method Not Found)", capabilityName, targetClass, targetName);
                        }
                        catch (Exception e)
                        {
                            FMLLog.log(Level.WARN, e, "Unable to inject capability %s at %s.%s", capabilityName, targetClass, targetName);
                        }
                        return null;
                    }
                });
            }
            else
            {
                list.add(new Function<Capability<?>, Object>()
                {
                    @Override
                    public Object apply(Capability<?> input)
                    {
                        try
                        {
                            Field field = Class.forName(targetClass).getDeclaredField(targetName);
                            if ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC)
                            {
                                FMLLog.log(Level.WARN, "Unable to inject capability %s at %s.%s (Non-Static)", capabilityName, targetClass, targetName);
                                return null;
                            }
                            EnumHelper.setFailsafeFieldValue(field, null, input);
                        }
                        catch (Exception e)
                        {
                            FMLLog.log(Level.WARN, e, "Unable to inject capability %s at %s.%s", capabilityName, targetClass, targetName);
                        }
                        return null;
                    }
                });
            }
        }
    }
}
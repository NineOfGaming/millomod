package net.millo.millomod.mod.features;


import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.PacketListener;
import net.minecraft.network.packet.Packet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketHandler {

    private final Map<Class<?>, List<Method>> subscribers = new HashMap<>();
    private final Map<Method, Feature> methodReference = new HashMap<>();


    public <T extends Feature> void register(T subscriber) {
        for (Method method : subscriber.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(PacketListener.class)) {
                Class<?> eventType = method.getParameterTypes()[0];
                subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(method);
                methodReference.put(method, subscriber);
            }
        }
    }

    public <E extends Packet<?>> boolean handlePacket(E packet) {
        List<Method> subscriberMethods = subscribers.get(packet.getClass());
        if (subscriberMethods == null) return false;
        boolean re = false;
        for (Method method : subscriberMethods) {
            try {
                boolean result = (boolean) method.invoke(methodReference.get(method), packet);
                if (result) re = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return re;
    }

}

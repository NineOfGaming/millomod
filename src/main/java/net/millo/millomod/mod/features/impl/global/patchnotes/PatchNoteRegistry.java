package net.millo.millomod.mod.features.impl.global.patchnotes;

import net.millo.millomod.mod.features.impl.global.patchnotes.impl.Patch173;
import net.millo.millomod.mod.features.impl.global.patchnotes.impl.Patch174;
import net.millo.millomod.mod.features.impl.global.patchnotes.impl.PatchBefore;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PatchNoteRegistry {

    private static final ArrayList<PatchNotes> patchNotes = new ArrayList<>();
    static {
//        Reflections reflections = new Reflections("net.millo.millomod.mod.features.impl.global.patchnotes.impl");
//        Set<Class<? extends PatchNotes>> classes = reflections.getSubTypesOf(PatchNotes.class);
//        for (Class<? extends PatchNotes> clazz : classes) {
//            try {
//                PatchNotes patchNote = clazz.getDeclaredConstructor().newInstance();
//                patchNotes.add(patchNote);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        patchNotes.addAll(List.of(
                new PatchBefore(),
                new Patch173(),
                new Patch174()
        ));

    }

    public static ArrayList<PatchNotes> getPatchNotes() {
        return patchNotes;
    }

    public static PatchNotes get(String modVersion) {
        for (PatchNotes patchNote : patchNotes) {
            if (patchNote.getVersion().equals(modVersion)) {
                return patchNote;
            }
        }
        return null;
    }
}

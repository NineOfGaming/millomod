package net.millo.millomod.mod.features.impl.global.patchnotes.impl;

import net.millo.millomod.mod.features.impl.global.patchnotes.PatchNotes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class Patch174 extends PatchNotes {

    @Override
    public @NotNull String getVersion() {
        return "1.7.4";
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public ArrayList<PatchNote> getContent() {
        return generateContent(
                PatchNote.header("New Features"),
                PatchNote.feature("Mod Menu Integration", "Added support for Mod Menu, allowing users to access MilloMod settings through hte Mod Menu menu. Using the Mod Menu API."),

                PatchNote.header("Bug Fixes"),
                PatchNote.change("Finally updated lang files"),
                PatchNote.bugFix("Patch Notes Content GUI", "Fixed an issue where the content GUI would not display correctly."),
                PatchNote.bugFix("Spectator Toggle", "Fixed an issue where the spectator toggle would not work while in spectator."),
                PatchNote.bugFix("Lagslayer Overlay", "Fixed getting stuck at 0.")
        );
    }

}

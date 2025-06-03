package net.millo.millomod.mod.features.impl.global.patchnotes;

import net.millo.millomod.mod.util.gui.GUIStyles;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class PatchNotes {

    @NotNull
    public abstract String getVersion();

    public abstract String getTitle();

    public abstract ArrayList<PatchNote> getContent();

    public String getName() {
        if (getTitle() != null && !getTitle().isEmpty()) {
            return getTitle();
        }
        return "v" + getVersion();
    }

    //

    protected ArrayList<PatchNote> generateContent(PatchNote... notes) {
        return new ArrayList<>(Arrays.asList(notes));
    }

    public SerializedPatchNotes serialize() {
        return new SerializedPatchNotes(getVersion(), getTitle(),getContent().stream()
                .map(PatchNote::serialize).toArray(SerializedPatchNote[]::new));
    }
    public static PatchNotes deserialize(SerializedPatchNotes in) {
        ArrayList<PatchNote> notes = new ArrayList<>(Arrays.stream(in.patchNotes).map(PatchNote::deserialize).toList());
        return new PatchNotes() {
            @Override
            public @NotNull String getVersion() {
                return in.version;
            }

            @Override
            public String getTitle() {
                return in.title;
            }

            @Override
            public ArrayList<PatchNote> getContent() {
                return notes;
            }
        };
    }

    // Single feature / bug fix
    public static class PatchNote {

        public enum Type {
            FEATURE(Text.literal("⭐").setStyle(GUIStyles.ADDED.getStyle())),
            BUG_FIX(Text.literal("✔").setStyle(GUIStyles.BUG.getStyle())),
            CHANGE(Text.literal("✎").setStyle(GUIStyles.CHANGED.getStyle())),
            REMOVED(Text.literal("❌").setStyle(GUIStyles.REMOVED.getStyle())),

            HEADER(Text.literal("»").setStyle(GUIStyles.HEADER.getStyle()));

            private final Text prefix;
            Type(Text prefix) {
                this.prefix = prefix;
            }

            public Text getPrefix() {
                return prefix;
            }
        }

        private final Type type;
        private final String feature;
        private final String description;

        public PatchNote(Type type, String feature) {
            this(type, feature, null);
        }

        public PatchNote(Type type, String feature, String description) {
            this.type = type;
            this.feature = feature;
            this.description = description;
        }

        public Text getText() {
            MutableText text = Text.literal(" ").append(type.getPrefix())
                    .append(Text.literal(" " + feature).setStyle(
                            type == Type.HEADER ? GUIStyles.ACTION.getStyle() : GUIStyles.NAME.getStyle()
                    ));
            if (description != null && !description.isEmpty()) {
                text.append(Text.literal(": " + description));
            }
            return text.setStyle(GUIStyles.UNSAVED.getStyle());
        }

        public SerializedPatchNote serialize() {
            return new SerializedPatchNote(type.name(), feature, description);
        }
        public static PatchNote deserialize(SerializedPatchNote in) {
            return new PatchNote(Type.valueOf(in.type), in.feature, in.description);
        }

        ///

        public static PatchNote feature(String feature) {
            return new PatchNote(Type.FEATURE, feature);
        }
        public static PatchNote feature(String feature, String description) {
            return new PatchNote(Type.FEATURE, feature, description);
        }

        public static PatchNote bugFix(String feature) {
            return new PatchNote(Type.BUG_FIX, feature);
        }
        public static PatchNote bugFix(String feature, String description) {
            return new PatchNote(Type.BUG_FIX, feature, description);
        }

        public static PatchNote change(String feature) {
            return new PatchNote(Type.CHANGE, feature);
        }
        public static PatchNote change(String feature, String description) {
            return new PatchNote(Type.CHANGE, feature, description);
        }

        public static PatchNote removed(String feature) {
            return new PatchNote(Type.REMOVED, feature);
        }
        public static PatchNote removed(String feature, String description) {
            return new PatchNote(Type.REMOVED, feature, description);
        }

        public static PatchNote header(String feature) {
            return new PatchNote(Type.HEADER, feature);
        }
        public static PatchNote header(String feature, String description) {
            return new PatchNote(Type.HEADER, feature, description);
        }

        public Type getType() {
            return type;
        }
    }


    public static class SerializedPatchNotes {
        public String version;
        public String title;
        public SerializedPatchNote[] patchNotes;

        public SerializedPatchNotes(String version, String title, SerializedPatchNote[] notes) {
            this.version = version;
            this.title = title;
            patchNotes = notes;
        }
    }

    public static class SerializedPatchNote {
        public String type;
        public String feature;
        public String description;

        public SerializedPatchNote(String type, String feature, String description) {
            this.type = type;
            this.feature = feature;
            this.description = description;
        }
    }

}

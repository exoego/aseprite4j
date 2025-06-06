package net.exoego.aseprite4j;

import java.util.EnumSet;
import java.util.Set;

public enum LayerFlag {
    VISIBLE(1),
    EDITABLE(2),
    LOCK_MOVEMENT(4),
    BACKGROUND(8),
    PREFER_LINKED_CELS(16),
    LAYER_GROUP_SHOULD_BE_DISPLAYED_COLLAPSED(32),
    REFERENCE_LAYER(64);

    private final int value;

    LayerFlag(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Set<LayerFlag> from(int value) {
        EnumSet<LayerFlag> flags = EnumSet.noneOf(LayerFlag.class);
        for (var flag : LayerFlag.values()) {
            if ((value & flag.getValue()) != 0) {
                flags.add(flag);
            }
        }
        return flags;
    }
}

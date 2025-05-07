package net.exoego.aseprite4j;

import java.util.EnumSet;

public enum HeaderFlag {
    /**
     * 1 = Layer opacity has valid value
     */
    LayerOpacityValid(1),

    /**
     *    2 =  Layer blend mode/opacity is valid for groups
     *                (composite groups separately first when rendering)
     */
    LayerBlendModeOpacityValid(2),

    /**
     * 4 = Layers have an UUID
     */
    LayerHaveUUID(4);

    private final int value;

    HeaderFlag(int value) {
        this.value = value;
    }

    public static EnumSet<HeaderFlag> setFrom(long value) {
        EnumSet<HeaderFlag> flags = EnumSet.noneOf(HeaderFlag.class);
        for (HeaderFlag flag : HeaderFlag.values()) {
            if ((value & flag.value) != 0) {
                flags.add(flag);
            }
        }
        return flags;
    }
}

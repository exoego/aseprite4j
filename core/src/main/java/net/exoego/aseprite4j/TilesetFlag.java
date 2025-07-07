package net.exoego.aseprite4j;

import java.util.EnumSet;
import java.util.Set;

public enum TilesetFlag {
    INCLUDE_LINK_TO_EXTERNAL_FILE(1),
    INCLUDE_TILES_INSIDE_THIS_FILE(2),
    USE_TILE_ID_0_AS_EMPTY(4),
    X_FLIPPED_VERSION_WILL_BE_TRIED_TO_MATCH_WITH(8),
    Y_FLIPPED_VERSION_WILL_BE_TRIED_TO_MATCH_WITH(16),
    DIAGONAL_FLIPPED_VERSION_WILL_BE_TRIED_TO_MATCH_WITH(32);

    private final int value;

    TilesetFlag(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Set<TilesetFlag> from(long value) {
        Set<TilesetFlag> flags = EnumSet.noneOf(TilesetFlag.class);
        for (TilesetFlag flag : TilesetFlag.values()) {
            if ((value & flag.getValue()) != 0) {
                flags.add(flag);
            }
        }
        return flags;
    }
}

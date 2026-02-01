package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

public class LayerFlagTest {

    @Test
    void fromZeroReturnsEmptySet() {
        var flags = LayerFlag.from(0);
        assertThat(flags).isEmpty();
    }

    @Test
    void fromSingleFlag() {
        assertThat(LayerFlag.from(1)).containsExactly(LayerFlag.VISIBLE);
        assertThat(LayerFlag.from(2)).containsExactly(LayerFlag.EDITABLE);
        assertThat(LayerFlag.from(4)).containsExactly(LayerFlag.LOCK_MOVEMENT);
        assertThat(LayerFlag.from(8)).containsExactly(LayerFlag.BACKGROUND);
        assertThat(LayerFlag.from(16)).containsExactly(LayerFlag.PREFER_LINKED_CELS);
        assertThat(LayerFlag.from(32)).containsExactly(LayerFlag.LAYER_GROUP_SHOULD_BE_DISPLAYED_COLLAPSED);
        assertThat(LayerFlag.from(64)).containsExactly(LayerFlag.REFERENCE_LAYER);
    }

    @Test
    void fromMultipleFlags() {
        // VISIBLE | EDITABLE = 1 | 2 = 3
        var flags = LayerFlag.from(3);
        assertThat(flags).containsExactly(LayerFlag.VISIBLE, LayerFlag.EDITABLE);
    }

    @Test
    void fromAllFlags() {
        // All flags: 1 + 2 + 4 + 8 + 16 + 32 + 64 = 127
        var flags = LayerFlag.from(127);
        assertThat(flags).containsExactly(
                LayerFlag.VISIBLE,
                LayerFlag.EDITABLE,
                LayerFlag.LOCK_MOVEMENT,
                LayerFlag.BACKGROUND,
                LayerFlag.PREFER_LINKED_CELS,
                LayerFlag.LAYER_GROUP_SHOULD_BE_DISPLAYED_COLLAPSED,
                LayerFlag.REFERENCE_LAYER
        );
    }

    @Test
    void fromCommonCombinations() {
        // VISIBLE | EDITABLE | BACKGROUND = 1 | 2 | 8 = 11
        var flags = LayerFlag.from(11);
        assertThat(flags).containsExactly(LayerFlag.VISIBLE, LayerFlag.EDITABLE, LayerFlag.BACKGROUND);
    }

    @Test
    void getValueReturnsCorrectBit() {
        assertThat(LayerFlag.VISIBLE.getValue()).isEqualTo(1);
        assertThat(LayerFlag.EDITABLE.getValue()).isEqualTo(2);
        assertThat(LayerFlag.LOCK_MOVEMENT.getValue()).isEqualTo(4);
        assertThat(LayerFlag.BACKGROUND.getValue()).isEqualTo(8);
        assertThat(LayerFlag.PREFER_LINKED_CELS.getValue()).isEqualTo(16);
        assertThat(LayerFlag.LAYER_GROUP_SHOULD_BE_DISPLAYED_COLLAPSED.getValue()).isEqualTo(32);
        assertThat(LayerFlag.REFERENCE_LAYER.getValue()).isEqualTo(64);
    }
}

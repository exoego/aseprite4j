package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LayerBlendModeTest {

    @ParameterizedTest
    @CsvSource({
            "0, NORMAL",
            "1, MULTIPLY",
            "2, SCREEN",
            "3, OVERLAY",
            "4, DARKEN",
            "5, LIGHTEN",
            "6, COLOR_DODGE",
            "7, COLOR_BURN",
            "8, HARD_LIGHT",
            "9, SOFT_LIGHT",
            "10, DIFFERENCE",
            "11, EXCLUSION",
            "12, HUE",
            "13, SATURATION",
            "14, COLOR",
            "15, LUMINOSITY",
            "16, ADDITION",
            "17, SUBTRACT",
            "18, DIVIDE"
    })
    void fromValueReturnsCorrectEnum(int value, String expectedName) {
        var mode = LayerBlendMode.from(value);
        assertThat(mode.name()).isEqualTo(expectedName);
        assertThat(mode.getValue()).isEqualTo(value);
    }

    @Test
    void fromInvalidValueThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> LayerBlendMode.from(-1));
        assertThrows(IllegalArgumentException.class, () -> LayerBlendMode.from(19));
        assertThrows(IllegalArgumentException.class, () -> LayerBlendMode.from(100));
    }

    @Test
    void allEnumValuesAreMapped() {
        for (var mode : LayerBlendMode.values()) {
            var fromValue = LayerBlendMode.from(mode.getValue());
            assertThat(fromValue).isEqualTo(mode);
        }
    }
}

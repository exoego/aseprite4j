package net.exoego.aseprite4j;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record TagsChunk(Map<String, Tag> tags) implements FrameChunk {
    static TagsChunk build(InputStreamReader reader) throws IOException {
        var numberOfTags = reader.WORD();

        // reserved
        reader.skip(8);

        if (numberOfTags == 0) {
            return new TagsChunk(Map.of());
        }

        var tags = new HashMap<String, Tag>(numberOfTags);
        for (int i = 0; i < numberOfTags; i++) {
            var fromFrame = reader.WORD();
            var toFrame = reader.WORD();
            var loopAnimationDirection = LoopAnimationDirection.from((byte) reader.BYTE());
            var animationRepeatCount = reader.WORD();

            // reserved
            reader.skip(6);

            // deprecated
            reader.readNBytes(3);

            // extra byte
            reader.skip(1);

            var name = reader.STRING();
            tags.put(name, new Tag(name, fromFrame, toFrame, loopAnimationDirection, animationRepeatCount));
        }
        return new TagsChunk(Collections.unmodifiableMap(tags));
    }

    public record Tag(String name, int fromFrame, int toFrame, LoopAnimationDirection loopAnimationDirection, int animationRepeatCount) {
    }
}

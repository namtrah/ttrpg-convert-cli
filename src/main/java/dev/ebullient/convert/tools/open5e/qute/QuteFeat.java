package dev.ebullient.convert.tools.open5e.qute;

import dev.ebullient.convert.tools.Tags;
import dev.ebullient.convert.tools.open5e.Open5eSources;
import io.quarkus.qute.TemplateData;

/**
 * 5eTools feat and optional feat attributes ({@code feat2md.txt})
 * <p>
 * Extension of {@link dev.ebullient.convert.tools.open5e.qute.Open5eQuteBase}.
 * </p>
 */
@TemplateData
public class QuteFeat extends Open5eQuteBase {

    /** Prerequisite level */
    public final String level;
    /** Formatted text listing other prerequisite conditions (optional) */
    public final String prerequisite;

    public QuteFeat(Open5eSources sources, String name, String source,
            String prerequisite, String level,
            String text, Tags tags) {
        super(sources, name, source, text, tags);
        withTemplate("feat2md.txt"); // Feat and OptionalFeature
        this.level = level;
        this.prerequisite = prerequisite; // optional
    }
}

package dev.ebullient.convert.tools.open5e.qute;

import dev.ebullient.convert.tools.CompendiumSources;
import dev.ebullient.convert.tools.Tags;
import io.quarkus.qute.TemplateData;

/**
 * 5eTools hazard attributes ({@code hazard2md.txt})
 * <p>
 * Extension of {@link dev.ebullient.convert.tools.open5e.qute.Open5eQuteBase}.
 * </p>
 */
@TemplateData
public class QuteHazard extends Open5eQuteBase {

    /** Type of hazard: "Magical Trap", "Wilderness Hazard" */
    public final String hazardType;

    public QuteHazard(CompendiumSources sources, String name, String source,
            String hazardType,
            String text, Tags tags) {
        super(sources, name, source, text, tags);
        this.hazardType = hazardType;
        withTemplate("hazard2md.txt"); // not trap or hazard (types)
    }
}

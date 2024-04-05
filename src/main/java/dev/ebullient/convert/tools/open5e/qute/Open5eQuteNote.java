package dev.ebullient.convert.tools.open5e.qute;

import java.util.Collection;
import java.util.List;

import dev.ebullient.convert.qute.QuteBase;
import dev.ebullient.convert.qute.QuteNote;
import dev.ebullient.convert.tools.CompendiumSources;
import dev.ebullient.convert.tools.Tags;
import dev.ebullient.convert.tools.open5e.Open5eSources;
import io.quarkus.qute.TemplateData;

/**
 * Attributes for notes that are generated from the 5eTools data.
 * This is a trivial extension of {@link dev.ebullient.convert.qute.QuteNote}.
 * <p>
 * Notes created from {@code Open5eQuteNote} will use the {@code note2md.txt} template.
 * </p>
 */
@TemplateData
public class Open5eQuteNote extends QuteNote {

    String targetPath;
    String filename;
    String template;

    public Open5eQuteNote(CompendiumSources sources, String name, String sourceText, String text, Tags tags) {
        super(sources, name, sourceText, text, tags);
    }

    public Open5eQuteNote(String name, String sourceText, String text, Tags tags) {
        super(name, sourceText, text, tags);
    }

    public Open5eQuteNote(String name, String sourceText, List<String> text, Tags tags) {
        super(name, sourceText, text, tags);
    }

    public Open5eQuteNote withTargetFile(String filename) {
        this.filename = filename;
        return this;
    }

    public String targetFile() {
        return filename == null ? super.targetFile() : filename;
    }

    public Open5eQuteNote withTargetPath(String path) {
        this.targetPath = path;
        return this;
    }

    public String targetPath() {
        return targetPath == null ? super.targetPath() : targetPath;
    }

    public Open5eQuteNote withTemplate(String template) {
        this.template = template;
        return this;
    }

    public String template() {
        return template == null ? super.template() : template;
    }

    public Collection<QuteBase> inlineNotes() {
        return sources() == null
                ? List.of()
                : Open5eSources.getInlineNotes(sources().getKey());
    }
}

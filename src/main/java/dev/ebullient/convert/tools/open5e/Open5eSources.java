package dev.ebullient.convert.tools.open5e;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import dev.ebullient.convert.config.TtrpgConfig;
import dev.ebullient.convert.io.FontRef;
import dev.ebullient.convert.io.Tui;
import dev.ebullient.convert.qute.ImageRef;
import dev.ebullient.convert.qute.QuteBase;
import dev.ebullient.convert.tools.CompendiumSources;
import dev.ebullient.convert.tools.IndexType;
import dev.ebullient.convert.tools.JsonNodeReader;
import dev.ebullient.convert.tools.JsonTextConverter.SourceField;
import dev.ebullient.convert.tools.ToolsIndex.TtrpgValue;
import dev.ebullient.convert.tools.open5e.JsonSource.JsonMediaHref;
import dev.ebullient.convert.tools.open5e.JsonSource.TableFields;
import io.quarkus.qute.TemplateData;

@TemplateData
public class Open5eSources extends CompendiumSources {

    private static final Map<String, Open5eSources> keyToSources = new HashMap<>();
    private static final Map<String, ImageRef> imageSourceToRef = new HashMap<>();
    private static final Map<String, FontRef> fontSourceToRef = new HashMap<>();
    private static final Map<String, List<QuteBase>> keyToInlineNotes = new HashMap<>();

    public static Open5eSources findSources(String key) {
        return keyToSources.get(key);
    }

    public static Open5eSources findSources(JsonNode node) {
        String key = TtrpgValue.indexKey.getTextOrEmpty(node);
        return keyToSources.get(key);
    }

    public static Open5eSources constructSources(JsonNode node) {
        if (node == null) {
            throw new IllegalArgumentException("Must pass a JsonNode");
        }
        String key = TtrpgValue.indexKey.getTextOrEmpty(node);
        if (key == null) {
            throw new IllegalArgumentException("Node has not been indexed (no key)");
        }
        Open5eIndexType type = Open5eIndexType.getTypeFromKey(key);
        return keyToSources.computeIfAbsent(key, k -> {
            Open5eSources s = new Open5eSources(type, key, node);
            s.checkKnown();
            return s;
        });
    }

    public static Open5eSources findOrTemporary(JsonNode node) {
        if (node == null) {
            throw new IllegalArgumentException("Must pass a JsonNode");
        }
        Open5eIndexType type = Open5eIndexType.getTypeFromNode(node);
        if (type == null) {
            type = SourceField.source.existsIn(node)
                    ? Open5eIndexType.reference
                    : Open5eIndexType.syntheticGroup;
        }
        String key = TtrpgValue.indexKey.getTextOrNull(node);
        if (key == null) {
            key = type.createKey(node);
        }
        Open5eSources sources = findSources(key);
        return sources == null
                ? new Open5eSources(type, key, node)
                : sources;
    }

    public static Collection<ImageRef> getImages() {
        return imageSourceToRef.values();
    }

    public static Collection<QuteBase> getInlineNotes(String key) {
        return keyToInlineNotes.getOrDefault(key, List.of());
    }

    public void addInlineNote(QuteBase note) {
        keyToInlineNotes.computeIfAbsent(this.key, k -> new ArrayList<>()).add(note);
    }

    public static Collection<FontRef> getFonts() {
        return fontSourceToRef.values().stream()
                .filter(FontRef::hasTextReference)
                .toList();
    }

    public static void addFonts(JsonNode source, JsonNodeReader field) {
        if (field.isArrayIn(source)) {
            for (JsonNode font : field.iterateArrayFrom(source)) {
                addFont(font.asText());
            }
        } else if (field.isObjectIn(source)) {
            for (Entry<String, JsonNode> font : field.iterateFieldsFrom(source)) {
                addFont(font.getKey(), font.getValue().asText());
            }
        }
    }

    static void addFont(String fontFamily, String fontString) {
        FontRef ref = FontRef.of(fontFamily, fontString);
        if (ref == null) {
            Tui.instance().warnf("Font '%s' is invalid, empty, or not found", fontString);
        } else {
            FontRef previous = fontSourceToRef.putIfAbsent(fontFamily, ref);
            if (previous != null) {
                Tui.instance().warnf("Font '%s' is already defined as '%s'", fontString, previous);
            }
        }
    }

    static void addFont(String fontString) {
        String fontFamily = FontRef.fontFamily(fontString);
        addFont(fontFamily, fontString);
    }

    public static String getFontReference(String fontString) {
        String fontFamily = FontRef.fontFamily(fontString);
        FontRef ref = fontSourceToRef.get(fontFamily);
        if (ref == null) {
            return null;
        }
        ref.addTextReference();
        return fontFamily;
    }

    final boolean srd;
    final boolean basicRules;
    final Open5eIndexType type;

    private Open5eSources(Open5eIndexType type, String key, JsonNode jsonElement) {
        super(type, key, jsonElement);
        this.type = type;
        this.basicRules = jsonElement.has("basicRules") && jsonElement.get("basicRules").asBoolean(false);
        this.srd = jsonElement.has("srd") && jsonElement.get("srd").asBoolean(false);
    }

    @Override
    public Open5eIndexType getType() {
        return type;
    }

    @Override
    protected boolean isSynthetic() {
        return type == Open5eIndexType.syntheticGroup;
    }

    public String getSourceText(boolean useSrd) {
        if (useSrd) {
            return "SRD / Basic Rules";
        }
        return sourceText;
    }

    public JsonNode findNode() {
        return Open5eIndex.getInstance().getNode(this.key);
    }

    protected String findName(IndexType type, JsonNode jsonElement) {
        if (type == Open5eIndexType.syntheticGroup) {
            return this.key.replaceAll(".+?\\|([^|]+).*", "$1");
        }
        return SourceField.name.getTextOrDefault(jsonElement,
                SourceField.abbreviation.getTextOrDefault(jsonElement,
                        TableFields.caption.getTextOrDefault(jsonElement,
                                "unknown")));
    }

    @Override
    protected String findSourceText(IndexType type, JsonNode jsonElement) {
        if (type == Open5eIndexType.syntheticGroup) {
            return this.key.replaceAll(".*\\|([^|]+)\\|", "$1");
        }
        String srcText = super.findSourceText(type, jsonElement);

        boolean basicRules = jsonElement.has("basicRules") && jsonElement.get("basicRules").asBoolean(false);
        String value = jsonElement.has("srd") ? jsonElement.get("srd").asText() : null;
        boolean srd = !(value == null || "false".equals(value));
        String srdValue = srd && !"true".equals(value) ? " (as '" + value + "')" : "";

        String srdBasic = "";
        if (srd && basicRules) {
            srdBasic = "Available in the SRD and the Basic Rules" + srdValue + ".";
        } else if (srd) {
            srdBasic = "Available in the SRD" + srdValue + ".";
        } else if (basicRules) {
            srdBasic = "Available in the Basic Rules" + srdValue + ".";
        }

        String sourceText = String.join(", ", srcText);
        if (srdBasic.isBlank()) {
            return sourceText;
        }
        return sourceText.isEmpty()
                ? srdBasic
                : sourceText + ". " + srdBasic;
    }

    @Override
    protected boolean datasourceFilter(String source) {
        return !List.of("phb", "mm", "dmg").contains(source.toLowerCase());
    }

    public Optional<String> uaSource() {
        Optional<String> source = sources.stream().filter(x -> x.contains("UA") && !x.equals("UAWGE")).findFirst();
        return source.map(TtrpgConfig::sourceToAbbreviation);
    }

    public ImageRef buildTokenImageRef(Open5eIndex index, String sourcePath, Path target, boolean useCompendium) {
        String key = sourcePath.toString();
        ImageRef imageRef = new ImageRef.Builder()
                .setRelativePath(target)
                .setInternalPath(sourcePath)
                .setRootFilepath(useCompendium ? index.compendiumFilePath() : index.rulesFilePath())
                .setVaultRoot(useCompendium ? index.compendiumVaultRoot() : index.rulesVaultRoot())
                .build(imageSourceToRef.get(key));
        imageSourceToRef.putIfAbsent(key, imageRef);
        return imageRef;
    }

    public ImageRef buildImageRef(Open5eIndex index, JsonMediaHref mediaHref, String imageBasePath, boolean useCompendium) {
        final String title = mediaHref.title == null ? "" : mediaHref.title;
        final String altText = mediaHref.altText == null ? title : mediaHref.altText;
        final String key = mediaHref.href.path == null
                ? mediaHref.href.url
                : mediaHref.href.path;

        if (mediaHref.href.url == null && mediaHref.href.path == null) {
            Tui.instance().errorf("We have an ImageRef (%s) with no path", mediaHref);
            ImageRef imageRef = new ImageRef.Builder()
                    .setTitle(index.replaceText(altText))
                    .build();
            return imageRef;
        }

        String fullPath = mediaHref.href.path == null
                ? mediaHref.href.url
                : mediaHref.href.path.replace("\\", "/");
        int pos = fullPath.lastIndexOf('/');
        String fileName = fullPath.substring(pos + 1);

        if (type == Open5eIndexType.deity || type == Open5eIndexType.note || type == Open5eIndexType.variantrule) {
            fileName = primarySource() + "-" + fileName;
        }

        int x = fileName.lastIndexOf('.');
        fileName = x < 0
                ? index.slugify(fileName)
                : index.slugify(fileName.substring(0, x)) + fileName.substring(x);
        Path target = Path.of(imageBasePath, "img", fileName);

        ImageRef.Builder builder = new ImageRef.Builder()
                .setWidth(mediaHref.width)
                .setTitle(index.replaceText(altText))
                .setRelativePath(target)
                .setRootFilepath(useCompendium ? index.compendiumFilePath() : index.rulesFilePath())
                .setVaultRoot(useCompendium ? index.compendiumVaultRoot() : index.rulesVaultRoot());

        if (mediaHref.href.path == null) {
            builder.setUrl(mediaHref.href.url);
        } else {
            builder.setInternalPath(mediaHref.href.path);
        }

        ImageRef imageRef = builder.build(imageSourceToRef.get(key));
        imageSourceToRef.putIfAbsent(key, imageRef);
        return imageRef;
    }

    /** Amend optionalfeaturetype with sources of related optional features */
    public void amendSources(Open5eSources otherSources) {
        this.sources.addAll(otherSources.sources);
        this.bookRef.addAll(otherSources.bookRef);
    }

    @Override
    public boolean includedBy(Set<String> sources) {
        return super.includedBy(sources) ||
                (TtrpgConfig.getConfig().noSources() && (this.srd || this.basicRules));
    }

    public boolean contains(Open5eSources sources) {
        Collection<String> sourcesList = sources.getSources();
        return this.sources.stream().anyMatch(sourcesList::contains);
    }
}

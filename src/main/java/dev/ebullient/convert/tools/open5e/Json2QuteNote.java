package dev.ebullient.convert.tools.open5e;

import com.fasterxml.jackson.databind.JsonNode;

import dev.ebullient.convert.tools.Tags;
import dev.ebullient.convert.tools.open5e.qute.Open5eQuteBase;
import dev.ebullient.convert.tools.open5e.qute.Open5eQuteNote;

public class Json2QuteNote extends Json2QuteCommon {

    final String title;
    boolean useSuffix;

    Json2QuteNote(Open5eIndex index, Open5eIndexType type, JsonNode jsonNode) {
        super(index, type, jsonNode);
        title = index.replaceText(jsonNode.get("name").asText());
    }

    Json2QuteNote useSuffix(boolean useSuffix) {
        this.useSuffix = useSuffix;
        return this;
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    protected Open5eQuteNote buildQuteNote() {
        Tags tags = new Tags(getSources());
        String targetFile = useSuffix
                ? Open5eQuteBase.fixFileName(getName(), getSources())
                : null;

        return new Open5eQuteNote(title,
                getSourceText(sources),
                getText("##"),
                tags)
                .withTargetFile(targetFile);
    }
}

package dev.ebullient.convert.tools.open5e;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import dev.ebullient.convert.tools.Tags;
import dev.ebullient.convert.tools.open5e.qute.Open5eQuteBase;
import dev.ebullient.convert.tools.open5e.qute.Open5eQuteNote;

public class Json2QuteTable extends Json2QuteCommon {

    Json2QuteTable(Open5eIndex index, Open5eIndexType type, JsonNode jsonNode) {
        super(index, type, jsonNode);
    }

    @Override
    public Open5eQuteNote buildQuteNote() {
        String key = getSources().getKey();
        if (index.isExcluded(key)) {
            return null;
        }

        Tags tags = new Tags(getSources());
        List<String> text = new ArrayList<>();
        String targetDir = type.getRelativePath();
        String targetFile = null;

        if (getName().equals("Damage Types")) {
            for (JsonNode row : iterableElements(rootNode.get("rows"))) {
                ArrayNode cols = (ArrayNode) row;
                maybeAddBlankLine(text);
                text.add("## " + replaceText(cols.get(0).asText()));
                maybeAddBlankLine(text);
                appendToText(text, cols.get(1), null);
            }
            targetDir = null;
        } else if (type == Open5eIndexType.tableGroup) {
            appendToText(text, Open5eFields.tables.getFrom(rootNode), "##");
        } else {
            targetFile = Open5eQuteBase.fixFileName(getName(), getSources());
            appendTable(text, rootNode);
        }

        return new Open5eQuteNote(getName(), getSourceText(sources), text, tags)
                .withTargetPath(targetDir)
                .withTargetFile(targetFile);
    }
}

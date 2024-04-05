package dev.ebullient.convert.tools.open5e;

import com.fasterxml.jackson.databind.JsonNode;

import dev.ebullient.convert.tools.Tags;
import dev.ebullient.convert.tools.open5e.qute.Open5eQuteBase;
import dev.ebullient.convert.tools.open5e.qute.QuteFeat;

public class Json2QuteOptionalFeature extends Json2QuteCommon {
    public Json2QuteOptionalFeature(Open5eIndex index, Open5eIndexType type, JsonNode jsonNode) {
        super(index, type, jsonNode);
    }

    @Override
    protected Open5eQuteBase buildQuteResource() {
        Tags tags = new Tags(getSources());

        for (String featureType : Open5eFields.featureType.getListOfStrings(rootNode, tui())) {
            tags.add("optional-feature", featureType);
        }

        // set the template to use
        return new QuteFeat(getSources(),
                getSources().getName(),
                getSourceText(sources),
                listPrerequisites(rootNode),
                null,
                getText("##"),
                tags);
    }
}

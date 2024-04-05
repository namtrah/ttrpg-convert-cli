package dev.ebullient.convert.tools.open5e;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import dev.ebullient.convert.tools.JsonNodeReader;
import dev.ebullient.convert.tools.Tags;
import dev.ebullient.convert.tools.open5e.ItemProperty.PropertyEnum;
import dev.ebullient.convert.tools.open5e.qute.QuteReward;

public class Json2QuteReward extends Json2QuteCommon {

    public Json2QuteReward(Open5eIndex index, Open5eIndexType type, JsonNode jsonNode) {
        super(index, type, jsonNode);
    }

    @Override
    protected QuteReward buildQuteResource() {
        Tags tags = new Tags(getSources());

        for (String type : SourceField.type.getListOfStrings(rootNode, tui())) {
            tags.add("reward", type);
        }

        List<String> details = new ArrayList<>();

        String type = RewardField.type.getTextOrNull(rootNode);
        if (type != null) {
            details.add(type);
        }
        PropertyEnum rarity = PropertyEnum.fromValue(RewardField.rarity.getTextOrNull(rootNode));
        if (rarity != null) {
            details.add(rarity.toString());
        }
        String detail = String.join(", ", details);

        return new QuteReward(getSources(),
                getSources().getName(),
                getSourceText(sources),
                RewardField.ability.transformTextFrom(rootNode, "\n", index),
                getSources().getName().startsWith(detail) ? "" : detail,
                RewardField.signaturespells.transformTextFrom(rootNode, "\n", index),
                getText("##"),
                tags);
    }

    enum RewardField implements JsonNodeReader {
        ability,
        rarity,
        signaturespells,
        type
    }
}

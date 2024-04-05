package dev.ebullient.convert.tools.open5e;

import com.fasterxml.jackson.databind.JsonNode;

import dev.ebullient.convert.tools.JsonNodeReader;
import dev.ebullient.convert.tools.Tags;
import dev.ebullient.convert.tools.open5e.qute.Open5eQuteBase;
import dev.ebullient.convert.tools.open5e.qute.QuteHazard;

public class Json2QuteHazard extends Json2QuteCommon {

    public Json2QuteHazard(Open5eIndex index, Open5eIndexType type, JsonNode jsonNode) {
        super(index, type, jsonNode);
    }

    @Override
    protected Open5eQuteBase buildQuteResource() {
        Tags tags = new Tags(getSources());

        String hazardType = TrapHazFields.trapHazType.getTextOrNull(rootNode);
        if (hazardType != null) {
            tags.add("hazard", hazardType);
        }

        return new QuteHazard(getSources(),
                getSources().getName(),
                getSourceText(getSources()),
                getHazardType(hazardType),
                getText("##"),
                tags);
    }

    enum TrapHazFields implements JsonNodeReader {
        trapHazType
    }

    String getHazardType(String key) {
        if (key == null) {
            return "Generic Hazard";
        }
        return switch (key) {
            case "ENV" -> "Environmental Hazard";
            case "EST" -> "Eldritch Storm";
            case "MAG" -> "Magical Trap";
            case "MECH" -> "Mechanical Trap";
            case "WTH" -> "Weather";
            case "WLD" -> "Wilderness Hazard";
            default -> "Generic Hazard"; // also "GEN"
        };
    }
}

package content.area.karamja.tai_bwo_wannai

import content.entity.player.dialogue.type.statement
import content.quest.quest
import world.gregs.voidps.engine.Script
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.entity.obj.replace
import world.gregs.voidps.engine.inv.add
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.timer.toTicks
import java.util.concurrent.TimeUnit

class JunglePotionHerbs : Script {

    init {
        herbSpot("marshy_jungle_vine", "marshy_jungle_vine_picked", "grimy_snake_weed", "started")
        herbSpot("palm_tree_ardrigal", "palm_tree_ardrigal_picked", "grimy_ardrigal", "snake_weed")
        herbSpot("scorched_earth", "scorched_earth_picked", "grimy_sito_foil", "ardrigal")
        herbSpot("volencia_moss_rocks", "volencia_moss_rocks_picked", "grimy_volencia_moss", "sito_foil")
        herbSpot("fungus_covered_cavern_wall", "fungus_pattern", "grimy_rogues_purse", "volencia_moss")
    }

    private fun herbSpot(obj: String, pickedObj: String, herb: String, requiredStage: String) {
        objectOperate("Search", obj) { (target) ->
            val stage = quest("jungle_potion")
            if (stage != requiredStage && stage != "completed") {
                message("There's nothing of interest here.")
                return@objectOperate
            }
            if (inventory.isFull()) {
                message("You don't have enough inventory space to carry the herb.")
                return@objectOperate
            }
            statement("You search and find a herb.")
            inventory.add(herb)
            target.replace(pickedObj, ticks = TimeUnit.SECONDS.toTicks(60))
        }
    }
}

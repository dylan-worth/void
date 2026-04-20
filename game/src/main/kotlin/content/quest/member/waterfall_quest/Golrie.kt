package content.quest.member.waterfall_quest

import content.entity.player.dialogue.Happy
import content.entity.player.dialogue.Idle
import content.entity.player.dialogue.Neutral
import content.entity.player.dialogue.Sad
import content.entity.player.dialogue.type.item
import content.entity.player.dialogue.type.npc
import content.entity.player.dialogue.type.player
import content.entity.player.inv.item.addOrDrop
import content.quest.quest
import content.quest.refreshQuestJournal
import world.gregs.voidps.engine.Script
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.inv.carriesItem

class Golrie : Script {

    init {
        npcOperate("Talk-to", "golrie") {
            when (quest("waterfall_quest")) {
                "found_book" -> givePebble()
                "has_pebble", "completed" -> {
                    if (!carriesItem("glarials_pebble")) {
                        lostPebble()
                    } else {
                        alreadyHasPebble()
                    }
                }
                else -> genericGreeting()
            }
        }
    }

    suspend fun Player.givePebble() {
        npc<Sad>("Oh, thank goodness! I thought I would be stuck in here forever.")
        player<Neutral>("Are you alright? What happened?")
        npc<Sad>("I was captured by those hobgoblins out there and thrown in this cell. I have been here for weeks.")
        npc<Idle>("I do not have much to offer you, but I have a small pebble that belonged to the elven queen Glarial. It is said to be the key to her tomb.")
        npc<Idle>("Please, take it. I have no use for it, and perhaps it will help you on your adventure.")
        addOrDrop("glarials_pebble")
        item("glarials_pebble", 600, "Golrie hands you Glarial's Pebble.")
        npc<Happy>("Her tomb is on a small island to the west of Baxtorian Falls. Use the pebble on the tombstone there to gain entry.")
        set("waterfall_quest", "has_pebble")
        refreshQuestJournal()
    }

    suspend fun Player.lostPebble() {
        npc<Idle>("You seem to have lost the pebble I gave you.")
        player<Idle>("Could I have another one?")
        npc<Idle>("I am afraid I only had the one. You will need to find it yourself.")
    }

    suspend fun Player.alreadyHasPebble() {
        npc<Happy>("You still have the pebble! Good luck with your adventure.")
        player<Happy>("Thank you, I will make good use of it.")
    }

    suspend fun Player.genericGreeting() {
        npc<Sad>("Please, help me get out of here!")
        player<Neutral>("I'm sorry, I can't help right now.")
        npc<Sad>("Those hobgoblins locked me in here. I have been trapped for days...")
    }
}

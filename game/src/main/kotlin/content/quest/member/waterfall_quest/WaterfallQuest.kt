package content.quest.member.waterfall_quest

import content.entity.player.dialogue.type.item
import content.entity.player.dialogue.type.statement
import content.entity.player.inv.item.addOrDrop
import content.quest.letterScroll
import content.quest.quest
import content.quest.questComplete
import content.quest.questJournal
import content.quest.refreshQuestJournal
import world.gregs.voidps.engine.Script
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.dialogue.talkWith
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.entity.character.jingle
import world.gregs.voidps.engine.entity.character.move.tele
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.exp.exp
import world.gregs.voidps.engine.event.AuditLog
import world.gregs.voidps.engine.inv.carriesItem
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.remove
import world.gregs.voidps.engine.inv.transact.operation.AddItem.add
import world.gregs.voidps.engine.queue.softQueue

class WaterfallQuest : Script {

    init {
        questJournalOpen("waterfall_quest") {
            val lines = when (quest("waterfall_quest")) {
                "completed" -> listOf(
                    "<str>I spoke to Almera who was worried about her son",
                    "<str>Hudon exploring the waterfall.",
                    "<str>I found a Book on Baxtorian in Almera's house.",
                    "<str>I traded the book to Rasolo in exchange for",
                    "<str>Glarial's Pebble.",
                    "<str>I entered Glarial's Tomb and retrieved Glarial's",
                    "<str>Amulet, Glarial's Urn, and a key.",
                    "<str>I entered the Waterfall Dungeon and performed the",
                    "<str>ritual at the Altar of Baxtorian.",
                    "",
                    "<red>QUEST COMPLETE!",
                    "",
                )
                "has_pebble" -> {
                    val list = mutableListOf(
                        "<str>I spoke to Almera who was worried about her son",
                        "<str>Hudon exploring the waterfall.",
                        "<str>I found a Book on Baxtorian in Almera's house.",
                        "<str>I traded the book to Rasolo in exchange for",
                        "<str>Glarial's Pebble.",
                        "",
                    )
                    val hasAmulet = carriesItem("glarials_amulet")
                    val hasUrn = carriesItem("glarials_urn_empty")
                    val hasKey = carriesItem("a_key")
                    if (!hasAmulet || !hasUrn || !hasKey) {
                        list.add("<navy>I need to enter <maroon>Glarial's Tomb <navy>by using the pebble")
                        list.add("<navy>on the <maroon>Tombstone <navy>on the island west of the falls.")
                        list.add("<navy>Inside I must find:")
                        if (!hasAmulet) list.add("<maroon>Glarial's Amulet <navy>from the coffin.")
                        if (!hasUrn) list.add("<maroon>Glarial's Urn <navy>from a crate.")
                        if (!hasKey) list.add("<maroon>A Key <navy>from the bookcase.")
                    } else {
                        list.add("<navy>I have <maroon>Glarial's Amulet<navy>, <maroon>Glarial's Urn")
                        list.add("<navy>and <maroon>a Key<navy>. I should swim to the <maroon>rock <navy>or")
                        list.add("<navy>use a <maroon>rope <navy>and climb the <maroon>dead tree <navy>by the falls.")
                        list.add("<navy>Inside, I need 6 of each rune on 3 pedestals:")
                        list.add("<maroon>Air<navy>, <maroon>Earth <navy>and <maroon>Water<navy>.")
                        list.add("<navy>Then use <maroon>Glarial's Urn <navy>on the <maroon>Altar of Baxtorian<navy>.")
                    }
                    list
                }
                "found_book" -> listOf(
                    "<str>I spoke to Almera who was worried about her son",
                    "<str>Hudon exploring the waterfall.",
                    "<str>I found a Book on Baxtorian in Almera's house.",
                    "",
                    "<navy>I should speak to <maroon>Rasolo the wandering merchant",
                    "<navy>near the river and show him the <maroon>Book on Baxtorian<navy>.",
                )
                "started" -> {
                    val foundHudon = get("waterfall_quest_hudon_found", false)
                    if (!foundHudon) {
                        listOf(
                            "<str>I spoke to Almera who was worried about her son",
                            "<str>Hudon exploring the waterfall.",
                            "",
                            "<navy>I should find <maroon>Hudon <navy>by the waterfall. I can",
                            "<navy>board the <maroon>raft <navy>near Almera's house to reach him.",
                        )
                    } else {
                        listOf(
                            "<str>I spoke to Almera who was worried about her son",
                            "<str>Hudon exploring the waterfall.",
                            "<str>I found Hudon safe by the waterfall.",
                            "",
                            "<navy>I should search the <maroon>bookcase <navy>in <maroon>Almera's house",
                            "<navy>for information about the waterfall.",
                        )
                    }
                }
                else -> listOf(
                    "<navy>I can start this quest by speaking to <maroon>Almera<navy>, who",
                    "<navy>lives in a house on the riverbank north of",
                    "<maroon>Baxtorian Falls<navy>.",
                )
            }
            questJournal("Waterfall Quest", lines)
        }

        // Board the raft to reach Hudon at the waterfall
        objectOperate("Board", "baxtorian_falls_raft") {
            val stage = quest("waterfall_quest")
            if (stage == "unstarted") {
                statement("The raft looks like it could take you to the waterfall, but you have no reason to go there.")
                return@objectOperate
            }
            message("You climb onto the raft and drift down towards the waterfall.")
            delay(2)
            tele(2512, 3480, 0)
            if (stage == "started" && !get("waterfall_quest_hudon_found", false)) {
                val hudon = NPCs.findOrNull(tile.regionLevel, "hudon_baxtorian_falls") ?: return@objectOperate
                talkWith(hudon)
            }
        }

        // Swim to the rock by the waterfall (brings you to the dead tree)
        objectOperate("Swim-to", "baxtorian_falls_rock") {
            message("You wade into the water and swim towards the rocky island.")
            delay(2)
            message("You haul yourself up onto the rock.")
            tele(2512, 3469, 0)
        }

        // Almera's bookcase gives Book on Baxtorian and a key
        objectOperate("Search", "almeras_bookcase") {
            when (quest("waterfall_quest")) {
                "started" -> {
                    if (!get("waterfall_quest_hudon_found", false)) {
                        statement("You should find Hudon first. Try the raft near the house.")
                        return@objectOperate
                    }
                    message("You search the bookcase and find an old book and a small key.")
                    addOrDrop("book_on_baxtorian")
                    addOrDrop("a_key")
                    item("book_on_baxtorian", 600, "You find a book about Baxtorian.")
                    set("waterfall_quest", "found_book")
                    refreshQuestJournal()
                }
                "found_book", "has_pebble" -> {
                    if (!carriesItem("book_on_baxtorian") && quest("waterfall_quest") == "found_book") {
                        message("You find another copy of the book.")
                        addOrDrop("book_on_baxtorian")
                    } else if (!carriesItem("a_key")) {
                        message("You find a spare key on the shelf.")
                        addOrDrop("a_key")
                    } else {
                        statement("You search the bookcase but find nothing more of interest.")
                    }
                }
                else -> statement("You search the bookcase but find nothing of interest.")
            }
        }

        // Read the Book on Baxtorian
        itemOption("Read", "book_on_baxtorian") {
            letterScroll(
                "Book on Baxtorian",
                listOf(
                    "",
                    "Baxtorian the Brave was the greatest of",
                    "the eleven elven chiefs. He led his people",
                    "south to push back the goblin armies of",
                    "Bandos.",
                    "",
                    "After years of battle he settled with his",
                    "wife Glarial in the great valley by the",
                    "river which the humans named after him.",
                    "",
                    "A great evil came upon the land and",
                    "Baxtorian led his army north to fight it.",
                    "He returned victorious, only to find his",
                    "people slain and Glarial taken.",
                    "",
                    "His grief was endless. He wandered the",
                    "wilderness for many years and was never",
                    "seen again.",
                    "",
                    "Legend says his spirit lingers in the",
                    "valley to this day, and that Glarial's",
                    "ghost guards her tomb.",
                ),
            )
        }

        // Enter Glarial's Tomb using the pebble on the tombstone
        itemOnObjectOperate("glarials_pebble", "glarials_tombstone") {
            val stage = quest("waterfall_quest")
            if (stage != "has_pebble" && stage != "completed") {
                statement("This tombstone seems significant, but you're not sure what to do.")
                return@itemOnObjectOperate
            }
            message("You press the pebble to the tombstone. Strange runes glow briefly...")
            delay(1)
            message("You feel yourself pulled underground.")
            tele(2524, 9840, 0)
        }

        // Exit Glarial's Tomb via the tombstone inside
        objectOperate("Exit", "glarials_tombstone") {
            if (tile.y > 5000) {
                message("You climb back up through the tomb entrance.")
                tele(2530, 3455, 0)
            }
        }

        // Search Glarial's Coffin for the amulet
        objectOperate("Search", "glarials_coffin") {
            val stage = quest("waterfall_quest")
            if (stage != "has_pebble" && stage != "completed") {
                statement("You have no reason to disturb this coffin.")
                return@objectOperate
            }
            if (!carriesItem("glarials_amulet")) {
                message("You search the coffin carefully...")
                delay(2)
                message("You find a bright green amulet inside.")
                addOrDrop("glarials_amulet")
                item("glarials_amulet", 600, "You find Glarial's Amulet!")
            } else {
                statement("You have already taken everything useful from this coffin.")
            }
        }

        // Search crate in Glarial's Tomb for the urn
        objectOperate("Search", "glarials_tomb_crate") {
            val stage = quest("waterfall_quest")
            if (stage != "has_pebble" && stage != "completed") {
                statement("There's nothing of interest in this crate.")
                return@objectOperate
            }
            if (!carriesItem("glarials_urn_empty")) {
                message("You rummage through the crate and find an ornate urn.")
                addOrDrop("glarials_urn_empty")
                item("glarials_urn_empty", 600, "You find Glarial's Urn!")
            } else {
                statement("There is nothing more of use in this crate.")
            }
        }

        // Search bookcase in Glarial's Tomb for a spare key
        objectOperate("Search", "glarials_tomb_bookcase") {
            val stage = quest("waterfall_quest")
            if (stage != "has_pebble" && stage != "completed") {
                statement("There is nothing of interest here.")
                return@objectOperate
            }
            if (!carriesItem("a_key")) {
                message("You search the bookcase and find a small key.")
                addOrDrop("a_key")
                item("a_key", 600, "You find a key!")
            } else {
                statement("There is nothing more of use on these shelves.")
            }
        }

        // Climb down dead tree into waterfall dungeon
        objectOperate("Climb", "baxtorian_falls_dead_tree") {
            val stage = quest("waterfall_quest")
            if (stage != "has_pebble" && stage != "completed") {
                statement("You'd rather not climb down into the waterfall.")
                return@objectOperate
            }
            if (!carriesItem("glarials_amulet")) {
                statement("An invisible force repels you. Perhaps you need something from Glarial's Tomb to enter.")
                return@objectOperate
            }
            if (!carriesItem("glarials_urn_empty")) {
                statement("You feel you're missing something. Glarial's Urn is needed to perform the ritual.")
                return@objectOperate
            }
            message("You clamber down the dead tree and drop into the waterfall.")
            delay(2)
            message("The current pulls you into the waterfall!")
            tele(2541, 9867, 0)
        }

        // Enter waterfall dungeon using rope on dead tree
        itemOnObjectOperate("rope", "baxtorian_falls_dead_tree") {
            val stage = quest("waterfall_quest")
            if (stage != "has_pebble" && stage != "completed") {
                statement("You'd rather not swing into the waterfall.")
                return@itemOnObjectOperate
            }
            if (!carriesItem("glarials_amulet")) {
                statement("An invisible force repels you. Perhaps you need something from Glarial's Tomb to enter.")
                return@itemOnObjectOperate
            }
            if (!carriesItem("glarials_urn_empty")) {
                statement("You feel you're missing something. Glarial's Urn is needed to perform the ritual.")
                return@itemOnObjectOperate
            }
            message("You tie the rope to the dead tree and swing out over the falls.")
            delay(2)
            message("The current pulls you into the waterfall!")
            tele(2541, 9867, 0)
        }

        // Open the waterfall dungeon door using the key
        objectOperate("Open", "waterfall_dungeon_door") {
            if (!carriesItem("a_key")) {
                statement("The door is sealed. You need a key to open it.")
                return@objectOperate
            }
            message("You use the key to unlock the stone door.")
            inventory.remove("a_key", 1)
            tele(2596, 9882, 0)
        }

        // Place runes on each pedestal
        for ((rune, pedestal) in RUNE_PEDESTALS) {
            itemOnObjectOperate(rune, pedestal) {
                if (quest("waterfall_quest") != "has_pebble") {
                    statement("Nothing happens.")
                    return@itemOnObjectOperate
                }
                if (get("${pedestal}_placed", false)) {
                    statement("This pedestal has already been activated.")
                    return@itemOnObjectOperate
                }
                if (!inventory.contains(rune, 6)) {
                    val runeName = rune.replace("_rune", " rune")
                    statement("You need 6 ${runeName}s to activate this pedestal.")
                    return@itemOnObjectOperate
                }
                val runeName = rune.replace("_rune", " rune")
                message("You place 6 ${runeName}s on the pedestal. It glows with a soft light.")
                inventory.remove(rune, 6)
                set("${pedestal}_placed", true)
            }
        }

        // Use Glarial's Urn on the Altar of Baxtorian to complete the quest
        itemOnObjectOperate("glarials_urn_empty", "baxtorian_altar") {
            if (quest("waterfall_quest") != "has_pebble") {
                statement("Nothing happens.")
                return@itemOnObjectOperate
            }
            if (!allPedestalsActivated()) {
                statement("You must place runes on all three pedestals before using the altar.")
                return@itemOnObjectOperate
            }
            message("You pour Glarial's ashes into the urn and place it on the altar.")
            delay(2)
            message("The room fills with a blinding golden light!")
            delay(1)
            message("You sense the spirit of Baxtorian finally at peace.")
            completeQuest()
        }
    }

    fun Player.allPedestalsActivated(): Boolean =
        RUNE_PEDESTALS.values.all { get("${it}_placed", false) }

    fun Player.completeQuest() {
        inventory.remove("glarials_urn_empty", 1)
        inventory.transaction {
            add("diamond")
            add("diamond")
            add("gold_bar")
            add("gold_bar")
            add("mithril_seeds")
        }
        AuditLog.event(this, "quest_completed", "waterfall_quest")
        set("waterfall_quest", "completed")
        jingle("quest_complete_1")
        exp(Skill.Attack, 13750.0)
        exp(Skill.Strength, 13750.0)
        refreshQuestJournal()
        inc("quest_points", 1)
        softQueue("quest_complete", 1) {
            questComplete(
                "Waterfall Quest",
                "1 Quest Point",
                "13,750 Attack XP",
                "13,750 Strength XP",
                "2 Diamonds",
                "2 Gold Bars",
                item = "glarials_amulet",
            )
        }
    }

    companion object {
        val RUNE_PEDESTALS = mapOf(
            "air_rune" to "baxtorian_pedestal_air",
            "earth_rune" to "baxtorian_pedestal_earth",
            "water_rune" to "baxtorian_pedestal_water",
        )
    }
}

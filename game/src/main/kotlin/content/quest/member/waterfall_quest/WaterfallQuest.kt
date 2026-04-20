package content.quest.member.waterfall_quest

import content.entity.combat.hit.damage
import content.entity.player.dialogue.Idle
import content.entity.player.dialogue.Neutral
import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.item
import content.entity.player.dialogue.type.statement
import content.entity.player.inv.item.addOrDrop
import content.quest.quest
import content.quest.questComplete
import content.quest.questJournal
import content.quest.refreshQuestJournal
import world.gregs.voidps.engine.Script
import world.gregs.voidps.engine.client.message
import world.gregs.voidps.engine.client.ui.dialogue.talkWith
import world.gregs.voidps.engine.entity.character.jingle
import world.gregs.voidps.engine.entity.character.move.tele
import world.gregs.voidps.engine.entity.character.npc.NPCs
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.character.player.skill.Skill
import world.gregs.voidps.engine.entity.character.player.skill.exp.exp
import world.gregs.voidps.engine.entity.item.Item
import world.gregs.voidps.engine.event.AuditLog
import world.gregs.voidps.engine.inv.Inventory
import world.gregs.voidps.engine.inv.carriesItem
import world.gregs.voidps.engine.inv.equipment
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.remove
import world.gregs.voidps.engine.inv.transact.operation.AddItem.add
import world.gregs.voidps.engine.queue.softQueue

class WaterfallQuest : Script {

    private val bannedCategories = setOf(
        "throwable", "arrow", "bolt",
        "magic_armour", "magic_weapon",
        "melee_armour_low", "melee_armour_mid", "melee_armour_high",
        "melee_weapon_low", "melee_weapon_mid", "melee_weapon_high",
        "prayer_armour", "prayer_consumable",
        "range_armour", "range_weapon",
    )

    init {
        questJournalOpen("waterfall_quest") {
            val lines = when (quest("waterfall_quest")) {
                "completed" -> listOf(
                    "<str>I spoke to Almera who was worried about her son",
                    "<str>Hudon exploring the waterfall.",
                    "<str>I found a Book on Baxtorian in Hadley's house.",
                    "<str>I freed Golrie from the gnome dungeon and he",
                    "<str>gave me Glarial's Pebble.",
                    "<str>I entered Glarial's Tomb and retrieved Glarial's",
                    "<str>Amulet and Glarial's Urn.",
                    "<str>I entered the Waterfall Dungeon and performed",
                    "<str>the ritual at the Chalice of Baxtorian.",
                    "",
                    "<red>QUEST COMPLETE!",
                    "",
                )
                "has_pebble" -> {
                    val list = mutableListOf(
                        "<str>I spoke to Almera who was worried about her son",
                        "<str>Hudon exploring the waterfall.",
                        "<str>I found a Book on Baxtorian in Hadley's house.",
                        "<str>I freed Golrie from the gnome dungeon and he",
                        "<str>gave me Glarial's Pebble.",
                        "",
                    )
                    val hasAmulet = carriesItem("glarials_amulet")
                    val hasUrn = carriesItem("glarials_urn")
                    if (!hasAmulet || !hasUrn) {
                        list.add("<navy>I need to enter <maroon>Glarial's Tomb <navy>by using the pebble")
                        list.add("<navy>on the <maroon>Tombstone <navy>on the island west of the falls.")
                        list.add("<navy>I must not bring weapons or armour. Inside I need:")
                        if (!hasAmulet) list.add("<maroon>Glarial's Amulet <navy>from the chest.")
                        if (!hasUrn) list.add("<maroon>Glarial's Urn <navy>from the coffin.")
                    } else {
                        list.add("<navy>I have <maroon>Glarial's Amulet <navy>and <maroon>Glarial's Urn<navy>.")
                        list.add("<navy>I should swim to the <maroon>rock <navy>or use a <maroon>rope")
                        list.add("<navy>on the <maroon>dead tree <navy>by the falls to enter.")
                        list.add("<navy>Inside, I need 6 each of <maroon>air<navy>, <maroon>earth")
                        list.add("<navy>and <maroon>water <navy>runes for the pedestals.")
                        list.add("<navy>Then use <maroon>Glarial's Amulet <navy>on the statue,")
                        list.add("<navy>and <maroon>Glarial's Urn <navy>on the <maroon>Chalice<navy>.")
                    }
                    list
                }
                "found_book" -> listOf(
                    "<str>I spoke to Almera who was worried about her son",
                    "<str>Hudon exploring the waterfall.",
                    "<str>I found a Book on Baxtorian in Hadley's house.",
                    "",
                    "<navy>The book mentions a gnome who has <maroon>Glarial's Pebble<navy>.",
                    "<navy>I should search the dungeon near the",
                    "<maroon>Tree Gnome Village <navy>to find him.",
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
                            "<navy>I should search the <maroon>bookcase <navy>in <maroon>Hadley's house",
                            "<navy>near the river for information about the waterfall.",
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
                refreshQuestJournal()
            }
        }

        // Swim to the rock by the waterfall
        objectOperate("Swim-to", "baxtorian_falls_rock") {
            message("You wade into the water and swim towards the rocky island.")
            delay(2)
            message("You haul yourself up onto the rock.")
            tele(2512, 3469, 0)
        }

        // Throw rope across to the rock
        itemOnObjectOperate("rope", "baxtorian_falls_rock") {
            message("You throw the rope over a ledge and swing across to the rocky island.")
            delay(2)
            message("You haul yourself up onto the rock.")
            tele(2512, 3469, 0)
        }

        // Hadley's bookcase gives Book on Baxtorian
        objectOperate("Search", "hadleys_bookcase") {
            when (quest("waterfall_quest")) {
                "started" -> {
                    message("You search the bookcase and find an interesting book.")
                    addOrDrop("book_on_baxtorian")
                    item("book_on_baxtorian", 600, "You find a book about Baxtorian.")
                    set("waterfall_quest", "found_book")
                    refreshQuestJournal()
                }
                "found_book", "has_pebble" -> {
                    if (!carriesItem("book_on_baxtorian") && quest("waterfall_quest") == "found_book") {
                        message("You find another copy of the book.")
                        addOrDrop("book_on_baxtorian")
                    } else {
                        statement("You search the bookcase but find nothing more of interest.")
                    }
                }
                else -> statement("You search the bookcase but find nothing of interest.")
            }
        }

        // Gnome dungeon bookcase — lore only
        objectOperate("Search", "gnome_dungeon_bookcase") {
            statement("You search the bookcase but find nothing of interest.")
        }

        // Gnome dungeon crate — gives key 293 to open the gate
        objectOperate("Search", "gnome_dungeon_crate") {
            val stage = quest("waterfall_quest")
            if (stage != "found_book" && stage != "has_pebble") {
                statement("There is nothing of interest in this crate.")
                return@objectOperate
            }
            if (carriesItem("a_key")) {
                statement("There is nothing more of use in this crate.")
                return@objectOperate
            }
            message("You rummage through the crate and find a small key.")
            addOrDrop("a_key")
            item("a_key", 600, "You find a key!")
        }

        // Gnome dungeon gate — needs key 293 to open; teleports player inside cell
        objectOperate("Open", "gnome_dungeon_gate") {
            if (!carriesItem("a_key")) {
                statement("The gate is locked. You need a key to open it.")
                return@objectOperate
            }
            message("You unlock the gate with the key.")
            inventory.remove("a_key", 1)
            message("The gate swings open.")
            delay(1)
            tele(2516, 9583, 0)
        }

        // Enter Glarial's Tomb — Entrana-style check, drains prayer, teleports inside
        itemOnObjectOperate("glarials_pebble", "glarials_tombstone") {
            val stage = quest("waterfall_quest")
            if (stage != "has_pebble" && stage != "completed") {
                statement("This tombstone seems significant, but you're not sure what to do.")
                return@itemOnObjectOperate
            }
            val forbidden = combatItemCheck(inventory) ?: combatItemCheck(equipment)
            if (forbidden != null) {
                statement("You cannot enter Glarial's Tomb with weapons or armour.")
                return@itemOnObjectOperate
            }
            message("You press the pebble to the tombstone. Strange runes glow briefly...")
            delay(1)
            message("You feel yourself pulled underground.")
            levels.set(Skill.Prayer, 0)
            tele(2555, 9844, 0)
        }

        // Exit Glarial's Tomb via the tombstone inside
        objectOperate("Exit", "glarials_tombstone") {
            if (tile.y > 5000) {
                message("You climb back up through the tomb entrance.")
                tele(2530, 3455, 0)
            }
        }

        // Chest near moss giants — gives Glarial's Amulet
        objectOperate("Search", "glarials_tomb_chest") {
            val stage = quest("waterfall_quest")
            if (stage != "has_pebble" && stage != "completed") {
                statement("You have no reason to search this chest.")
                return@objectOperate
            }
            if (!carriesItem("glarials_amulet")) {
                message("You search the chest carefully...")
                delay(2)
                message("You find a bright green amulet inside.")
                addOrDrop("glarials_amulet")
                item("glarials_amulet", 600, "You find Glarial's Amulet!")
            } else {
                statement("You have already taken everything useful from this chest.")
            }
        }

        // Glarial's Coffin — gives Glarial's Urn (full)
        objectOperate("Search", "glarials_coffin") {
            val stage = quest("waterfall_quest")
            if (stage != "has_pebble" && stage != "completed") {
                statement("You have no reason to disturb this coffin.")
                return@objectOperate
            }
            if (!carriesItem("glarials_urn")) {
                message("You search the coffin carefully...")
                delay(2)
                message("You find an urn filled with ashes inside.")
                addOrDrop("glarials_urn")
                item("glarials_urn", 600, "You find Glarial's Urn!")
            } else {
                statement("You have already taken everything useful from this coffin.")
            }
        }

        // Bookcase and crate in Glarial's Tomb — flavor only
        objectOperate("Search", "glarials_tomb_crate") {
            statement("There is nothing of use in this crate.")
        }

        objectOperate("Search", "glarials_tomb_bookcase") {
            statement("You search the bookcase but find nothing of interest.")
        }

        // Climb dead tree — warning; falling deals 8 damage
        objectOperate("Climb", "baxtorian_falls_dead_tree") {
            statement("It would be difficult to get down this tree without using a rope on it first.")
            choice {
                option<Neutral>("Climb down anyway.") {
                    message("You climb down the tree")
                    delay(4)
                    message("and lose your grip.")
                    message("You get washed away in the current.")
                    tele(2534, 3450, 0)
                    damage(8)
                }
                option<Idle>("Back away.") {
                    statement("You leave the tree alone.")
                }
            }
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
            if (!carriesItem("glarials_urn")) {
                statement("You feel you're missing something. Glarial's Urn is needed to perform the ritual.")
                return@itemOnObjectOperate
            }
            message("You tie the rope to the dead tree and swing out over the falls.")
            delay(2)
            message("The current pulls you into the waterfall!")
            tele(2541, 9867, 0)
        }

        // Waterfall dungeon crate — gives second key (298) to open the inner door
        objectOperate("Search", "waterfall_dungeon_crate") {
            if (quest("waterfall_quest") != "has_pebble") {
                statement("There is nothing of interest in this crate.")
                return@objectOperate
            }
            if (carriesItem("a_key_waterfall_dungeon")) {
                statement("There is nothing more of use in this crate.")
                return@objectOperate
            }
            message("You search the crate and find a key.")
            addOrDrop("a_key_waterfall_dungeon")
            item("a_key_waterfall_dungeon", 600, "You find a key!")
        }

        // Open the waterfall dungeon door using the dungeon key (298)
        objectOperate("Open", "waterfall_dungeon_door") {
            if (!carriesItem("a_key_waterfall_dungeon")) {
                statement("The door is sealed. You need a key to open it.")
                return@objectOperate
            }
            message("You use the key to unlock the stone door.")
            inventory.remove("a_key_waterfall_dungeon", 1)
            tele(2596, 9882, 0)
        }

        // Place runes on the pedestal — requires 6 of each type at once
        itemOnObjectOperate("air_rune", "baxtorian_rune_pedestal") { placeRunes() }
        itemOnObjectOperate("earth_rune", "baxtorian_rune_pedestal") { placeRunes() }
        itemOnObjectOperate("water_rune", "baxtorian_rune_pedestal") { placeRunes() }

        // Use Glarial's Amulet on the statue of Glarial
        itemOnObjectOperate("glarials_amulet", "baxtorian_statue") {
            if (quest("waterfall_quest") != "has_pebble") {
                statement("Nothing happens.")
                return@itemOnObjectOperate
            }
            if (!get("baxtorian_runes_placed", false)) {
                message("As you touch the statue, a bolt of energy blasts you back!")
                damage(20)
                return@itemOnObjectOperate
            }
            if (get("baxtorian_amulet_placed", false)) {
                statement("You have already placed the amulet on the statue.")
                return@itemOnObjectOperate
            }
            message("You place Glarial's Amulet on the statue. It glows with a warm light.")
            set("baxtorian_amulet_placed", true)
        }

        // Use Glarial's Urn on the Chalice of Baxtorian to complete the quest
        itemOnObjectOperate("glarials_urn", "baxtorian_chalice") {
            if (quest("waterfall_quest") != "has_pebble") {
                statement("Nothing happens.")
                return@itemOnObjectOperate
            }
            if (!get("baxtorian_runes_placed", false) || !get("baxtorian_amulet_placed", false)) {
                statement("You must place the runes on the pedestal and the amulet on the statue first.")
                return@itemOnObjectOperate
            }
            message("You place Glarial's Urn on the chalice.")
            delay(2)
            message("The room fills with a blinding golden light!")
            delay(1)
            message("You sense the spirit of Baxtorian finally at peace.")
            completeQuest()
        }
    }

    private fun Player.combatItemCheck(inv: Inventory): Item? {
        for (item in inv.items) {
            if (item.isEmpty()) continue
            val categories: Set<String> = item.def.getOrNull("categories") ?: continue
            if (bannedCategories.any { categories.contains(it) }) return item
        }
        return null
    }

    private suspend fun Player.placeRunes() {
        if (quest("waterfall_quest") != "has_pebble") {
            statement("Nothing happens.")
            return
        }
        if (get("baxtorian_runes_placed", false)) {
            statement("The pedestals have already been activated.")
            return
        }
        if (!inventory.contains("air_rune", 6) || !inventory.contains("earth_rune", 6) || !inventory.contains("water_rune", 6)) {
            statement("You need 6 air runes, 6 earth runes and 6 water runes to activate the pedestals.")
            return
        }
        message("You place 6 air runes, 6 earth runes and 6 water runes on the pedestals.")
        inventory.remove("air_rune", 6)
        inventory.remove("earth_rune", 6)
        inventory.remove("water_rune", 6)
        message("The pedestals glow with a soft light.")
        set("baxtorian_runes_placed", true)
    }

    private fun Player.completeQuest() {
        inventory.remove("glarials_urn", 1)
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
}

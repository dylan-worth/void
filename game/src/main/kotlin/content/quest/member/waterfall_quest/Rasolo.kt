package content.quest.member.waterfall_quest

import content.entity.player.dialogue.Disheartened
import content.entity.player.dialogue.Happy
import content.entity.player.dialogue.Idle
import content.entity.player.dialogue.Quiz
import content.entity.player.dialogue.type.choice
import content.entity.player.dialogue.type.item
import content.entity.player.dialogue.type.npc
import content.entity.player.dialogue.type.player
import content.quest.quest
import content.quest.refreshQuestJournal
import world.gregs.voidps.engine.Script
import world.gregs.voidps.engine.entity.character.player.Player
import world.gregs.voidps.engine.entity.item.floor.FloorItems
import world.gregs.voidps.engine.inv.add
import world.gregs.voidps.engine.inv.carriesItem
import world.gregs.voidps.engine.inv.inventory
import world.gregs.voidps.engine.inv.remove

class Rasolo : Script {

    init {
        npcOperate("Talk-to", "rasolo_baxtorian_falls") {
            when (quest("waterfall_quest")) {
                "found_book" -> {
                    if (carriesItem("book_on_baxtorian")) {
                        offerTrade()
                    } else {
                        npc<Idle>("I hear there's an old book about Baxtorian somewhere nearby. If you find it, I'd be very interested in making a trade.")
                    }
                }
                "has_pebble" -> {
                    if (!carriesItem("glarials_pebble")) {
                        npc<Idle>("Ah, I see you've misplaced the pebble I gave you.")
                        player<Disheartened>("Could I have another one?")
                        npc<Idle>("I do have a spare, as luck would have it.")
                        if (inventory.isFull()) {
                            npc<Idle>("You'll need to make some room in your pack first, though.")
                        } else {
                            inventory.add("glarials_pebble")
                            item("glarials_pebble", 600, "Rasolo hands you another pebble.")
                            npc<Idle>("Use it on Glarial's Tombstone, on the island to the west of the falls.")
                        }
                    } else {
                        npc<Idle>("Use the pebble on Glarial's Tombstone, on the small island to the west of the falls. That will grant you entry to her tomb.")
                    }
                }
                "completed" -> {
                    npc<Happy>("You've done it, haven't you? I can feel the difference in the air.")
                    player<Happy>("The ritual is complete. Baxtorian is at peace.")
                    npc<Idle>("A remarkable achievement. You should be proud of what you've accomplished.")
                }
                else -> defaultGreeting()
            }
        }
    }

    suspend fun Player.offerTrade() {
        npc<Happy>("Ah! Is that the Book on Baxtorian you're carrying?")
        player<Quiz>("Yes, I found it in Almera's house. Do you know something about it?")
        npc<Idle>("Indeed I do! Baxtorian was a great elven king who once ruled this valley. His wife Glarial was buried on a small island just west of here.")
        npc<Idle>("I happen to have something that belonged to Glarial. A small pebble engraved with elvish markings. I believe it's the key to her tomb.")
        choice {
            option<Happy>("I'll trade the book for the pebble.") {
                npc<Happy>("Excellent! A fair trade.")
                inventory.remove("book_on_baxtorian", 1)
                if (inventory.isFull()) {
                    FloorItems.add(tile, "glarials_pebble", disappearTicks = 300, owner = this)
                } else {
                    inventory.add("glarials_pebble")
                }
                item("glarials_pebble", 600, "Rasolo hands you Glarial's Pebble.")
                npc<Idle>("Use the pebble on Glarial's Tombstone. It's on a small island to the west of the waterfall. Good luck, adventurer.")
                set("waterfall_quest", "has_pebble")
                refreshQuestJournal()
            }
            option<Idle>("Not yet. I'd like to keep the book for now.") {
                npc<Idle>("Of course. Come back when you're ready to trade.")
            }
        }
    }

    suspend fun Player.defaultGreeting() {
        npc<Idle>("Good day, traveller. Can I interest you in some of my wares?")
        choice {
            option<Quiz>("What are you doing all the way out here?") {
                npc<Idle>("I wander these lands buying and selling curiosities. There is much of interest in this valley.")
                npc<Idle>("The legend of Baxtorian and his wife Glarial is particularly fascinating. I've spent years collecting artefacts related to their story.")
            }
            option<Idle>("No thank you, not right now.") {
                player<Idle>("Perhaps another time.")
                npc<Idle>("As you wish.")
            }
        }
    }
}
